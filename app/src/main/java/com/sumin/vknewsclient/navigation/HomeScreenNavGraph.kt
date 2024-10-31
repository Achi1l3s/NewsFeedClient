package com.sumin.vknewsclient.navigation

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.sumin.vknewsclient.domain.FeedPost

fun NavGraphBuilder.homeScreenNavGraph(
    newsFeedScreenContent: @Composable () -> Unit,
    commentsScreenContent: @Composable (FeedPost) -> Unit
) {
    navigation(
        startDestination = Screen.NewsFeed.route,
        route = Screen.Home.route
    ) {
        composable(route = Screen.NewsFeed.route) {
            newsFeedScreenContent()
        }
        composable(
            route = Screen.Comments.route,
            arguments = listOf(
                navArgument(
                    Screen.KEY_FEED_POST
                ) {
                    type = FeedPost.NavigationType
                }
            )
        ) {
            val args = it.arguments

            val feedPost = kotlin.runCatching {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Log.d("JSON Output","true" )
                    args?.getParcelable(Screen.KEY_FEED_POST)
                } else {
                    Log.d("JSON Output","else" )
                    args?.getParcelable(
                        Screen.KEY_FEED_POST,
                        FeedPost::class.java
                    )
                }
            }.getOrNull()
                ?: throw ClassCastException("Args is null")

            commentsScreenContent(feedPost)
        }
    }
}