package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class CommentsContentDto(
    @SerializedName("profiles") val profiles: List<ProfileDto>,
    @SerializedName("items") val comments: List<CommentDto>,
    @SerializedName("offset") val startCommentsFrom: Int?
)
