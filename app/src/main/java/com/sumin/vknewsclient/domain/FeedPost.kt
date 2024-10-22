package com.sumin.vknewsclient.domain

import com.sumin.vknewsclient.R

data class FeedPost(
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
)
