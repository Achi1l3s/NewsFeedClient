package com.faist.vknewsclient.utils

import java.util.Locale
import kotlin.random.Random

class RandomFillBlankInfo {

    fun getRandomTimeStamp(): String {
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
        return sentence.capitalize(Locale.ROOT) + "."
    }

    fun getFewSentences(count: Int): String {
        return (1..count).joinToString(" ") { getRandomSentence(Random.nextInt(5, 10)) }
    }
}