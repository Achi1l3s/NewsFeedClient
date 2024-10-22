package com.sumin.vknewsclient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sumin.vknewsclient.domain.FeedPost
import com.sumin.vknewsclient.domain.StatisticItem
import java.util.Locale
import kotlin.random.Random

class MainViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val initialList = mutableListOf<FeedPost>().apply {
        repeat(10) {
            add(
                FeedPost(
                    postDate = getRandomTimeStamp(),
                    postText = getFewSentences(Random.nextInt(3, 7))
                )
            )
        }
    }

    private val _feedPost = MutableLiveData(FeedPost())
    val feedPost: LiveData<FeedPost> = _feedPost

    fun updateCount(item: StatisticItem) {
        val oldStatistics = feedPost.value?.postStatistics ?: throw RuntimeException()
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
        _feedPost.value = newStatistics.let { feedPost.value?.copy(postStatistics = it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRandomTimeStamp(): String {
        val randomHours = Random.nextInt(0, 24)
        val randomMinutes = Random.nextInt(0, 60)

        return String.format(Locale.getDefault(), "%02d:%02d", randomHours, randomMinutes)
    }

    private fun getRandomSentence(wordCount: Int): String {
        val words = listOf(
            "awesome", "amazing", "great", "fantastic", "incredible", "wonderful", "beautiful",
            "magnificent", "interesting", "cool", "super", "fun", "exciting", "brilliant",
            "nice", "lovely", "fascinating", "unbelievable", "charming", "glorious", "fucking",
            "bitch", "shit", "stupid", "dick", "pussy", "people", "woman", "man", "children", "can be",
            "must be", "silly", "don't be", "but", "I'm", "He is", "She is", "They are", "We are", "It's", "is",
            "sort of", "life", "moment", "adventure", "journey", "dream", "happiness", "story",
            "experience", "path", "emotion", "freedom", "future", "world", "beauty", "day",
            "night", "truth", "light", "nature", "wonder", "will be"
        )

        val sentence = (1..wordCount).joinToString(" ") { words.random() }
        return sentence.capitalize() + "."
    }

    private fun getFewSentences(count: Int): String {
        return (1..count).joinToString(" ") { getRandomSentence(Random.nextInt(5, 10)) }
    }
}