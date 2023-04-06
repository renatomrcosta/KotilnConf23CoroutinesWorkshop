package com.kotlinconf.workshop.articles.tasks

import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.*
import com.kotlinconf.workshop.blog.ArticleInfo

// TODO
// Implement 'loadArticlesWithComments' in two versions: blocking and suspend

fun loadArticles(serviceBlocking: BlogServiceBlocking): List<Article> {
    TODO()
}

suspend fun loadArticles(service: BlogService): List<Article> {
    TODO()
}