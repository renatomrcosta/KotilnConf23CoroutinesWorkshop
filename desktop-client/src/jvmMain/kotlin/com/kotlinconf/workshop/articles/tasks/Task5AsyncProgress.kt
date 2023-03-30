package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.launch

fun BlogService.observeArticlesConcurrently(): Flow<Article> {
    TODO()
}

fun BlogService.observeArticlesConcurrentlyWithProgress(): Flow<List<Article>> {
    TODO()
}

