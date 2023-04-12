package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Task: Implement loading of articles and comments using flows (with progress!)
fun observeArticlesLoading(service: BlogService): Flow<Article> = runBlocking {
    service.getArticleInfoList().asFlow()
        .map {
            Article(
                info = it,
                comments = service.getComments(articleInfo = it),
            )
        }
}

/* Intended solution
fun observeArticlesLoading(service: BlogService): Flow<Article> = flow {
    service.getArticleInfoList()
        .forEach {
            emit(
                Article(
                    info = it,
                    comments = service.getComments(articleInfo = it),
                ),
            )
        }
}
 */
