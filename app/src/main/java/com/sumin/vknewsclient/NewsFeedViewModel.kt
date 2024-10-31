package com.sumin.vknewsclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sumin.vknewsclient.utils.RandomFillBlankInfo
import com.sumin.vknewsclient.domain.FeedPost
import com.sumin.vknewsclient.domain.StatisticItem
import com.sumin.vknewsclient.ui.theme.NewsFeedScreenState
import kotlin.random.Random

class NewsFeedViewModel : ViewModel() {

    private val fillBlankInfoCommentsAndTime = RandomFillBlankInfo()

    private val initialList = mutableListOf<FeedPost>().apply {
        repeat(10) {
            add(
                FeedPost(
                    id = it,
                    postDate = fillBlankInfoCommentsAndTime.getRandomTimeStamp(),
                    postText = fillBlankInfoCommentsAndTime.getFewSentences(Random.nextInt(1, 11))
                )
            )
        }
    }

    private val initialState = NewsFeedScreenState.Posts(posts = initialList)
    private val _screenState = MutableLiveData<NewsFeedScreenState>(initialState)
    val screenState: LiveData<NewsFeedScreenState> = _screenState

    fun updateCount(feedPost: FeedPost, item: StatisticItem) {
        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return
        val oldPosts = currentState.posts.toMutableList()
        val oldStatistics = feedPost.postStatistics
        val newStatistics = oldStatistics
            .toMutableList()
            .apply {
                replaceAll { oldItem ->
                    if (item.type == oldItem.type) {
                        oldItem.copy(count = oldItem.count + 1)
                    } else {
                        oldItem
                    }
                }
            }

        val newFeedPost = feedPost.copy(postStatistics = newStatistics)
        val newPosts = oldPosts.apply {
            replaceAll {
                if (it.id == newFeedPost.id) {
                    newFeedPost
                } else {
                    it
                }
            }
        }
        _screenState.value = NewsFeedScreenState.Posts(posts = newPosts)
    }

    fun removeFeedPost(feedPost: FeedPost) {
        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return
        val oldPosts = currentState.posts.toMutableList()
        oldPosts.remove(feedPost)
        _screenState.value = NewsFeedScreenState.Posts(posts = oldPosts)
    }
}