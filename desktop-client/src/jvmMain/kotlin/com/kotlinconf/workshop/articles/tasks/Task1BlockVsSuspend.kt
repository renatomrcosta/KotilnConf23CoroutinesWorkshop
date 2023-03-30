package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.*
import com.kotlinconf.workshop.blog.ArticleInfo

// TODO
// Implement 'loadArticlesWithComments' in two versions: blocking and suspend

fun BlogServiceBlocking.loadArticles(): List<Article> {
    TODO()
}

suspend fun BlogService.loadArticles(): List<Article> {
    TODO()
}