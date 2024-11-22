package com.faist.vknewsclient.presentation

import android.app.Application
import com.faist.vknewsclient.di.ApplicationComponent
import com.faist.vknewsclient.di.DaggerApplicationComponent
import com.faist.vknewsclient.domain.entity.FeedPost

class NewsFeedApplication: Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(
            this,
            FeedPost(
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                listOf(),
                false
            )
        )
    }
}