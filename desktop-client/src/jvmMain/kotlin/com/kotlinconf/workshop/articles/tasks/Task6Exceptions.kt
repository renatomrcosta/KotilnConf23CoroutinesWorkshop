package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun observeArticlesUnstable(service: BlogService): Flow<Article> = flow {
    service.getArticleInfoList().map {
        emit(
            Article(
                info = it,
                comments = service.getCommentsUnstable(articleInfo = it),
            ),
        )
    }
}
