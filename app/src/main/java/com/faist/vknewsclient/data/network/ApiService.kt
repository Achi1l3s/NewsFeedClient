package com.faist.vknewsclient.data.network

import com.faist.vknewsclient.data.model.CommentsResponseDto
import com.faist.vknewsclient.data.model.LikesCountResponseDto
import com.faist.vknewsclient.data.model.NewsFeedResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("newsfeed.get?v=5.199")
    suspend fun loadNewsFeed(
        @Query("access_token") token: String
    ): NewsFeedResponseDto

    @GET("newsfeed.get?v=5.199")
    suspend fun loadNewsFeed(
        @Query("access_token") token: String,
        @Query("start_from") startFrom: String
    ): NewsFeedResponseDto

    @GET("wall.getComments?v=5.199&extended=1&fields=photo_100&sort=desc")
    suspend fun loadComments(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("post_id") postId: Long,
        @Query("count") count: Int
    ): CommentsResponseDto

    @GET("wall.getComments?v=5.199&extended=1&fields=photo_100&sort=desc")
    suspend fun loadComments(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("post_id") postId: Long,
        @Query("count") count: Int,
        @Query("offset") startCommentsFrom: Int?
    ): CommentsResponseDto

    @GET("likes.add?v=5.199&type=post")
    suspend fun addLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("likes.delete?v=5.199&type=post")
    suspend fun deleteLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("newsfeed.ignoreItem?v=5.199&type=wall")
    suspend fun ignorePost(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    )
}