package com.faist.vknewsclient.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

fun <T> Flow<T>.mergeWith(anotherFlow: Flow<T>): Flow<T> {
    return merge(this, anotherFlow)
}