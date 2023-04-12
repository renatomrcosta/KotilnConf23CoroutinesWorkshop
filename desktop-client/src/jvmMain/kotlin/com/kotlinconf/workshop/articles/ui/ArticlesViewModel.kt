package com.kotlinconf.workshop.articles.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import com.kotlinconf.workshop.articles.network.BlogServiceBlocking
import com.kotlinconf.workshop.articles.tasks.loadArticlesConcurrently
import com.kotlinconf.workshop.articles.tasks.loadArticlesNonCancelable
import com.kotlinconf.workshop.articles.tasks.observeArticlesConcurrently
import com.kotlinconf.workshop.articles.tasks.observeArticlesLoading
import com.kotlinconf.workshop.articles.tasks.observeArticlesUnstable
import com.kotlinconf.workshop.articles.tasks.observeArticlesUnstableWithRetry
import com.kotlinconf.workshop.articles.ui.ArticlesViewModel.LoadingStatus.CANCELED
import com.kotlinconf.workshop.articles.ui.ArticlesViewModel.LoadingStatus.COMPLETED
import com.kotlinconf.workshop.articles.ui.ArticlesViewModel.LoadingStatus.FAILED
import com.kotlinconf.workshop.articles.ui.ArticlesViewModel.LoadingStatus.IN_PROGRESS
import com.kotlinconf.workshop.articles.ui.ArticlesViewModel.LoadingStatus.NOT_STARTED
import com.kotlinconf.workshop.articles.ui.LoadingMode.BLOCKING
import com.kotlinconf.workshop.articles.ui.LoadingMode.CONCURRENT
import com.kotlinconf.workshop.articles.ui.LoadingMode.CONCURRENT_WITH_PROGRESS
import com.kotlinconf.workshop.articles.ui.LoadingMode.NON_CANCELABLE
import com.kotlinconf.workshop.articles.ui.LoadingMode.SUSPENDING
import com.kotlinconf.workshop.articles.ui.LoadingMode.UNSTABLE_NETWORK
import com.kotlinconf.workshop.articles.ui.LoadingMode.UNSTABLE_WITH_RETRY
import com.kotlinconf.workshop.articles.ui.LoadingMode.WITH_PROGRESS
import com.kotlinconf.workshop.articles.util.loadStoredMode
import com.kotlinconf.workshop.articles.util.removeStoredMode
import com.kotlinconf.workshop.articles.util.saveParams
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.launch
import loadArticles

class ArticlesViewModel(
    private val blockingService: BlogServiceBlocking,
    private val service: BlogService,
    parentScope: CoroutineScope,
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        markLoadingCompletion(exception)
    }

    // Task: Use SupervisorJob to handle errors
    // Replace Job() with SupervisorJob() and make sure the app keeps working on a child failure (LoadingMode.UNSTABLE_NETWORK).
    private val scope = CoroutineScope(parentScope.coroutineContext + SupervisorJob() + coroutineExceptionHandler)

    var loadingMode by mutableStateOf(BLOCKING)
        private set

    fun changeLoadingMode(loadingMode: LoadingMode) {
        this.loadingMode = loadingMode
    }

    enum class LoadingStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELED, FAILED }

    var loadingStatus by mutableStateOf(NOT_STARTED)
        private set

    var currentLoadingTimeMillis by mutableStateOf(0L)
        private set

    var newLoadingEnabled by mutableStateOf(true)
        private set

    var cancellationEnabled by mutableStateOf(false)
        private set

    private var loadingJob by mutableStateOf<Job?>(null)

    private val _articlesFlow = MutableStateFlow(listOf<Article>())
    val articlesFlow: StateFlow<List<Article>> get() = _articlesFlow

    init {
        try {
            changeLoadingMode(loadStoredMode())
        } catch (e: Exception) {
            e.printStackTrace()
            removeStoredMode()
        }
    }

    fun loadComments() {
        saveParams(loadingMode)
        clearResults()
        cancellationEnabled = true
        loadingJob = scope.launch {
            val startTime = System.currentTimeMillis()
            when (loadingMode) {
                BLOCKING -> {
                    val articleList = loadArticles(blockingService)
                    updateResults(articleList, startTime)
                }

                SUSPENDING -> {
                    val articleList = loadArticles(service)
                    updateResults(articleList, startTime)
                }

                CONCURRENT -> {
                    val articleList = loadArticlesConcurrently(service)
                    updateResults(articleList, startTime)
                }

                NON_CANCELABLE -> {
                    val articleList = loadArticlesNonCancelable(service)
                    updateResults(articleList, startTime)
                }

                WITH_PROGRESS -> {
                    val articleFlow = observeArticlesLoading(service)
                    updateResultsWithProgress(articleFlow, startTime)
                }

                CONCURRENT_WITH_PROGRESS -> {
                    val articleFlow = observeArticlesConcurrently(service)
                    updateResultsWithProgress(articleFlow, startTime)
                }

                UNSTABLE_NETWORK -> {
                    val articles = observeArticlesUnstable(service)
                    updateResultsWithProgress(articles, startTime)
                }

                UNSTABLE_WITH_RETRY -> {
                    val articles = observeArticlesUnstableWithRetry(service)
                    updateResultsWithProgress(articles, startTime)
                }
            }
        }
    }

    private fun updateResults(
        articles: List<Article>,
        startTime: Long,
        completed: Boolean = true,
    ) {
        loadingStatus = if (completed) COMPLETED else IN_PROGRESS
        currentLoadingTimeMillis = System.currentTimeMillis() - startTime
        _articlesFlow.value = articles
        if (completed) {
            markLoadingCompletion()
        }
    }

    private suspend fun updateResultsWithProgress(
        articleFlow: Flow<Article>,
        startTime: Long,
    ) {
        articleFlow
            .runningFold(listOf<Article>()) { list, article -> list + article }
            .onEach { updateResults(it, startTime, completed = false) }
            .onCompletion { markLoadingCompletion(it) }
            .collect()
    }

    private fun markLoadingCompletion(throwable: Throwable? = null) {
        loadingStatus = when {
            throwable is CancellationException -> CANCELED
            throwable != null -> FAILED
            else -> COMPLETED
        }
        newLoadingEnabled = true
        cancellationEnabled = false
    }

    fun cancelLoading() {
        loadingJob?.cancel()
        loadingStatus = CANCELED
        newLoadingEnabled = true
        cancellationEnabled = false
        currentLoadingTimeMillis = 0
    }

    private fun clearResults() {
        _articlesFlow.value = listOf()
        loadingStatus = IN_PROGRESS
        newLoadingEnabled = false
        cancellationEnabled = false
        currentLoadingTimeMillis = 0
    }
}
