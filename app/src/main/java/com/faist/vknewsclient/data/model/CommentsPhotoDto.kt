package com.faist.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class CommentsPhotoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("albumId") val albumId: Long,
    @SerializedName("ownerId") val ownerId: Long,
    @SerializedName("sizes") val sizes: List<SizeDto>,
    @SerializedName("text") val text: String,
    @SerializedName("date") val date: Long
)