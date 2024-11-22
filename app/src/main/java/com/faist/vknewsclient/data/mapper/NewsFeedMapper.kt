package com.faist.vknewsclient.data.mapper

import android.util.Log
import com.faist.vknewsclient.data.model.CommentsResponseDto
import com.faist.vknewsclient.data.model.NewsFeedResponseDto
import com.faist.vknewsclient.domain.entity.FeedPost
import com.faist.vknewsclient.domain.entity.PostComment
import com.faist.vknewsclient.domain.entity.StatisticItem
import com.faist.vknewsclient.domain.entity.StatisticType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

class NewsFeedMapper @Inject constructor() {

    fun mapResponseToPosts(responseDto: NewsFeedResponseDto): List<FeedPost> {
        val result = mutableListOf<FeedPost>()

        val posts = responseDto.newsFeedContent.posts
        val groups = responseDto.newsFeedContent.groups
        val validPosts = posts.filter {
            it.views != null && it.reposts != null && it.comments != null && it.likes != null
        }

        for (post in validPosts) {
            val group = groups.find { it.id == post.communityId.absoluteValue } ?: continue
            val contentImgUrl = post.attachments?.firstOrNull()?.photo?.sizes?.lastOrNull()?.url
            Log.d("DebugGG", "Post ID: ${post.id}, Views: ${post.views}")
            val feedPost = FeedPost(
                id = post.id,
                communityId = post.communityId,
                communityName = group.name,
                communityImageUrl = group.imageUrl,
                publicationDate = mapTimestampToDate(post.date),
                contentText = post.text,
                contentImgUrl = contentImgUrl,
                statistics = listOf(
                    StatisticItem(type = StatisticType.VIEWS, post.views.count),
                    StatisticItem(type = StatisticType.SHARES, post.reposts.count),
                    StatisticItem(type = StatisticType.COMMENTS, post.comments.count),
                    StatisticItem(type = StatisticType.LIKES, post.likes.count)
                ),
                isLiked = post.likes.userLikes > 0
            )

            result.add(feedPost)
        }

        return result
    }

    fun mapResponseToComments(responseDto: CommentsResponseDto): List<PostComment> {
        val result = mutableListOf<PostComment>()

        val comments = responseDto.commentsContent.comments
        val profiles = responseDto.commentsContent.profiles

        for (comment in comments) {
            val profile = profiles.firstOrNull() { it.id == comment.authorId } ?: continue

            val postComment = PostComment(
                id = comment.authorId,
                authorName = "${
                    profile.firstName.replaceFirstChar { it.uppercase(Locale.ROOT) }
                } ${
                    profile.lastName.replaceFirstChar { it.uppercase(Locale.ROOT) }
                }",
                publicationDate = mapTimestampToDate(comment.date),
                commentText = comment.text,
                authorAvatarUrl = profile.avatarUrl
            )
            result.add(postComment)
        }
        return result
    }

    private fun mapTimestampToDate(millis: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = Date(millis * 1000)

        return dateFormat.format(date)
    }
}





























