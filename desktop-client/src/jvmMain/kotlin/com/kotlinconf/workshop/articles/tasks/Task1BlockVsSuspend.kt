import com.kotlinconf.workshop.articles.model.Article
import com.kotlinconf.workshop.articles.network.BlogService
import com.kotlinconf.workshop.articles.network.BlogServiceBlocking

// Task: Implement blocking and non-blocking versions for articles loading

// This function is invoked when you select the "BLOCKING" option in the UI.
fun loadArticles(serviceBlocking: BlogServiceBlocking): List<Article> =
    serviceBlocking.getArticleInfoList()
        .associateWith { articleInfo -> serviceBlocking.getComments(articleInfo = articleInfo) }
        .map { (articleInfo, comments) ->
            Article(
                info = articleInfo,
                comments = comments,
            )
        }

// This function is invoked when you select the "SUSPENDING" option in the UI.
suspend fun loadArticles(service: BlogService): List<Article> = service.getArticleInfoList()
    .associateWith { articleInfo -> service.getComments(articleInfo = articleInfo) }
    .map { (articleInfo, comments) ->
        Article(
            info = articleInfo,
            comments = comments,
        )
    }
