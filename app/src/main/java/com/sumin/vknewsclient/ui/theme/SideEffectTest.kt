package com.sumin.vknewsclient.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Handler

@Composable
fun SideEffectTest(number: MyNumber) {
    Column {
        LazyColumn {
            repeat(5) {
                item {
                    Text(text = "${number.a}: $it")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Handler().postDelayed({
            number.a = 7
        }, 3000)
        LazyColumn {
            repeat(5) {
                item {
                    Text(text = "${number.a}: $it")
                }
            }
        }
    }

}

data class MyNumber(var a: Int)