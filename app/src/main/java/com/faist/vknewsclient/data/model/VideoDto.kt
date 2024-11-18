package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("ownerId") val ownerId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("player") val player: String

)
