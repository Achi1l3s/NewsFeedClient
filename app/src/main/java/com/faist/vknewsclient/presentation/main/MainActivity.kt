package com.faist.vknewsclient.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faist.vknewsclient.ui.theme.VkNewsClientTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VkNewsClientTheme {
                val viewModel: MainViewModel = viewModel()
                val authState = viewModel.authState.observeAsState(AuthState.Initial)

                val launcher =
                    rememberLauncherForActivityResult(
                    contract = VK.getVKAuthActivityResultContract()
                ) {
                        viewModel.performAuthResult(it)
                }

                when(authState.value) {
                    is AuthState.Authorized -> MainScreen()
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
