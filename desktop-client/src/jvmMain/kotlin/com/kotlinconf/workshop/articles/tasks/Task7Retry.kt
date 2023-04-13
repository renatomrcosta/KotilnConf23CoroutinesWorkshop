package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import com.kotlinconf.workshop.blog.ArticleInfo
import com.kotlinconf.workshop.blog.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry

fun observeArticlesUnstableWithRetry(service: BlogService): Flow<Article> = flow {
    val articleInfoList = service.getArticleInfoList()
    for (articleInfo in articleInfoList) {
        val comments = flow { emit(getCommentsWithRetry(service, articleInfo)) }.retry(10) {
            println("Retrying again $it")
            true
        }.first()
        emit(Article(articleInfo, comments))
    }
}

suspend fun getCommentsWithRetry(service: BlogService, articleInfo: ArticleInfo): List<Comment> {
    return service.getCommentsUnstable(articleInfo)
}
