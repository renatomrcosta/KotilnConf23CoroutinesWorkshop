package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// Task: Implement concurrent loading of articles
suspend fun loadArticlesConcurrently(service: BlogService): List<Article> = coroutineScope {
    service.getArticleInfoList().associateWith { async { service.getComments(articleInfo = it) } }
        .map { (articleInfo, commentsDeferred) ->
            Article(
                info = articleInfo,
                comments = commentsDeferred.await(),
            )
        }
}
