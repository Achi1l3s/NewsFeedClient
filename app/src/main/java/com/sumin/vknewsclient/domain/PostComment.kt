package com.sumin.vknewsclient.domain

import com.sumin.vknewsclient.R

data class PostComment(
    val id: Int,
    val authorName: String = "Author",
    val publicationDate: String = "06:14",
    val commentText: String = "Some comment text",
    val authorAvatar: Int = R.drawable.comment_author_avatar
)