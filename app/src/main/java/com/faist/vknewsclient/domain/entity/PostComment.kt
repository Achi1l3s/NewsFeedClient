package com.faist.vknewsclient.domain.entity

import com.faist.vknewsclient.data.model.AttachmentDto

data class PostComment(
    val id: Long,
    val authorName: String,
    val publicationDate: String,
    val commentText: String,
    val authorAvatarUrl: String,
    val attachments: List<AttachmentDto>? = null
)