package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class CommentsResponseDto(
    @SerializedName("response") val commentsContent: CommentsContentDto
)
