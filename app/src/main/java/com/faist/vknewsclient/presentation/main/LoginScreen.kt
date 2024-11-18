package com.faist.vknewsclient.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.faist.vknewsclient.R
import com.faist.vknewsclient.ui.theme.DarkBlue

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    Log.d("VKLoginScreen", "LoginScreen in fun")
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp),
                painter = painterResource(id = R.drawable.vk_logo),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(100.dp))
            Log.d("VKLoginScreen", "LoginScreen before Button in fun")
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = DarkBlue,
                    contentColor = Color.White
                ),
                onClick = { onLoginClick() }
            ) {
                Log.d("VKLoginScreen", "LoginScreen in Button fun")
                Text(text = "Login")
            }
        }
    }
}