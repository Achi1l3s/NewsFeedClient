package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class AudioDto(
    @SerializedName("id") val id: Long,
    @SerializedName("ownerId") val ownerId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("url") val url: String
)
