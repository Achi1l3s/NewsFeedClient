package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class DocDto(
    @SerializedName("id") val id: Long,
    @SerializedName("ownerId") val ownerId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("size") val size: Long,
    @SerializedName("ext") val ext: String,
    @SerializedName("url") val url: String,
    @SerializedName("type") val type: Int
)
