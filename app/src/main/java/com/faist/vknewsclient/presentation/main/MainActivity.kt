package com.faist.vknewsclient.presentation.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faist.vknewsclient.domain.entity.AuthState
import com.faist.vknewsclient.presentation.NewsFeedApplication
import com.faist.vknewsclient.presentation.ViewModelFactory
import com.faist.vknewsclient.presentation.getApplicationComponent
import com.faist.vknewsclient.ui.theme.VkNewsClientTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MainActivity : ComponentActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
            val authState = viewModel.authState.collectAsState(AuthState.Initial)

            val launcher =
                rememberLauncherForActivityResult(
                    contract = VK.getVKAuthActivityResultContract()
                ) {
                    viewModel.performAuthResult()
                }
            VkNewsClientTheme {
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





fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        repeat(1000) {
            channel.send(1)
        }
        channel.close() // Закрываем канал после отправки
    }

    var counter = 0
    for (value in channel) {
        counter += value
    }

    println("Counter: $counter") // Всегда 1000
}

