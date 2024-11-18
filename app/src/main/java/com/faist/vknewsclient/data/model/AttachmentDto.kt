package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class AttachmentDto(
    @SerializedName("type") val type: String,
    @SerializedName("photo") val photo: CommentsPhotoDto? = null,
    @SerializedName("video") val video: VideoDto? = null,
    @SerializedName("audio") val audio: AudioDto? = null,
    @SerializedName("doc") val doc: DocDto? = null
)
