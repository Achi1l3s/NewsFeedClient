package com.sumin.vknewsclient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.sumin.vknewsclient.ui.theme.ActivityResultTest
import com.sumin.vknewsclient.ui.theme.MainScreen
import com.sumin.vknewsclient.ui.theme.VkNewsClientTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VkNewsClientTheme {
                val someState = remember {
                    mutableStateOf(true)
                }

                Log.d("VKMainActivity", "Recomposition: ${someState.value}")
                val launcher =
                    rememberLauncherForActivityResult(
                    contract = VK.getVKAuthActivityResultContract()
                ) {
                    when (it) {
                        is VKAuthenticationResult.Success -> {
                            Log.d("VKMainActivity", "success auth")
                        }

                        is VKAuthenticationResult.Failed -> {
                            Log.d("VKMainActivity", "failed auth")
                        }
                    }
                }
                LaunchedEffect(key1 = Unit) {
                    Log.d("VKMainActivity", "LaunchedEffect fun")
                }
                SideEffect {
                    Log.d("VKMainActivity", "SideEffect fun")
//                    launcher.launch(listOf(VKScope.WALL))
                }
                Button(onClick = { someState.value = !someState.value }) {

                }
            }
        }
    }
}
