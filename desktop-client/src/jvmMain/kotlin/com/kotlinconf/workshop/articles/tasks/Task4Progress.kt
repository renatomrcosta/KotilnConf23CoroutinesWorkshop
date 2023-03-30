package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.runningFold

fun BlogService.observeArticlesLoading(): Flow<Article> = flow {
    TODO()
}

fun BlogService.observeArticlesLoadingWithProgress(): Flow<List<Article>> {
    TODO()
}