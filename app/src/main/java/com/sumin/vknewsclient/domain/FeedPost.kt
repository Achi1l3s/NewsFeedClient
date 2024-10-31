package com.sumin.vknewsclient.domain

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import com.sumin.vknewsclient.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedPost(
    val id: Int = 0,
    val groupTitle: String = "/dev/null",
    val groupIcon: Int = R.drawable.post_comunity_thumbnail,
    val postDate: String = "14:07",
    val postText: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    val postImage: Int = R.drawable.post_content_image,
    val postStatistics: List<StatisticItem> = listOf(
        StatisticItem(StatisticType.VIEWS, 1487),
        StatisticItem(StatisticType.SHARES, 33),
        StatisticItem(StatisticType.COMMENTS, 50),
        StatisticItem(StatisticType.LIKES, 551),
    )
) : Parcelable {

    companion object {

        val NavigationType: NavType<FeedPost> = object : NavType<FeedPost>(false) {

            override fun get(bundle: Bundle, key: String): FeedPost? {
                return bundle.getParcelable(key)
            }

            override fun parseValue(value: String): FeedPost {
                return Gson().fromJson(value, FeedPost::class.java)
            }

            override fun put(bundle: Bundle, key: String, value: FeedPost) {
                bundle.putParcelable(key, value)
            }
        }
    }
}
