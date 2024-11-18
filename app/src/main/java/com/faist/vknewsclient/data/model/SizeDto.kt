package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class SizeDto(
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)
