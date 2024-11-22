package com.faist.vknewsclient.presentation.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faist.vknewsclient.domain.entity.AuthState
import com.faist.vknewsclient.presentation.NewsFeedApplication
import com.faist.vknewsclient.presentation.ViewModelFactory
import com.faist.vknewsclient.ui.theme.VkNewsClientTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (application as NewsFeedApplication).component
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            VkNewsClientTheme {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                val authState = viewModel.authState.collectAsState(AuthState.Initial)

                val launcher =
                    rememberLauncherForActivityResult(
                    contract = VK.getVKAuthActivityResultContract()
                ) {
                        viewModel.performAuthResult()
                }

                when(authState.value) {
                    is AuthState.Authorized -> MainScreen(viewModelFactory)
                    is AuthState.NotAuthorized -> {
                        LoginScreen {
                            Log.d("VKLoginScreen", "launcher start")
                            launcher.launch(listOf(VKScope.WALL, VKScope.FRIENDS))
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
