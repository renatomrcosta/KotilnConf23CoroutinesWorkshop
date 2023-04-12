package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

// Run both versions and try to cancel
suspend fun loadArticlesNonCancelable(service: BlogService): List<Article> {
    return service.getArticleInfoList()
        .associateWith { GlobalScope.async { service.getComments(articleInfo = it) } }
        .map { (articleInfo, commentsDeferred) ->
            Article(
                info = articleInfo,
                comments = commentsDeferred.await(),
            )
        }
}
