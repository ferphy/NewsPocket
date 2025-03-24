package com.example.newsapp.core.data

import com.example.newsapp.Constants.API_KEY
import com.example.newsapp.core.data.local.ArticleDao
import com.example.newsapp.core.data.remote.NewsListDto
import com.example.newsapp.core.domain.Article
import com.example.newsapp.core.domain.NewsList
import com.example.newsapp.core.domain.NewsRepository
import com.example.newsapp.core.domain.NewsResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(
    private val httpClient: HttpClient,
    private val dao : ArticleDao
) : NewsRepository{

    private val tag = "NewsRepository: "
    private val baseUrl = "https://newsdata.io/api/1/latest"
    private val apiKey = API_KEY

    private suspend fun getLocalNews(nextPage: String?) : NewsList{
        val localNews = dao.getArticleList()
        println(tag + "getLocalNews: " + localNews.size + "Nextpage: " + nextPage)

        val newsList = NewsList(
            articles = localNews.map { it.toArticle() },
            nextPage = nextPage
        )

        return newsList
    }

    private suspend fun getRemoteNews(nextPage: String?) : NewsList{
        val newsListDto : NewsListDto = httpClient.get(baseUrl){
            parameter("apiKey",apiKey)
            parameter("language","es")
            if(nextPage != null) parameter("page",nextPage)
        }.body()

        println(tag + "getLocalNews: " + newsListDto.results?.size + "Nextpage: " + nextPage)

        return newsListDto.toNewsList()

    }

    override suspend fun getNews(): Flow<NewsResult<NewsList>> {
        return flow {
            val remoteNewsList = try {
                getRemoteNews(null)
            }catch (e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw e
                println(tag + "getNews remote Exception" + e.message)
                null
            }

            remoteNewsList?.let{
                dao.clearDatabase()
                remoteNewsList.articles?.let { it1 -> dao.upsertArticleList(it1.map { it.toArticleEntity() }) }
                emit(NewsResult.Success(getLocalNews(remoteNewsList.nextPage)))
                return@flow
            }

            val localNewsList = getLocalNews(null)
            if(localNewsList.articles?.isNotEmpty() == true){
                emit(NewsResult.Success(getLocalNews(localNewsList.nextPage)))
                return@flow
            }

            emit(NewsResult.Error("No data"))
        }
    }

    override suspend fun paginate(nextPage: String?): Flow<NewsResult<NewsList>> {
        return flow {
            val remoteNewsList = try {
                getRemoteNews(nextPage)
            }catch (e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw e
                println(tag + "paginate remote Exception" + e.message)
                null
            }

            remoteNewsList?.let{
                remoteNewsList.articles?.let { it1 -> dao.upsertArticleList(it1.map { it.toArticleEntity() }) }

                //Not getting them from the database like getNews()
                //because we will also get old items that we already have before paginating
                emit(NewsResult.Success(remoteNewsList))
                return@flow
            }

        }
    }

    override suspend fun getArticle(articleId: String): Flow<NewsResult<Article>> {
        return flow{
            dao.getArticle(articleId)?.let{ article ->
                println(tag + "get local article" + article.articleId)
                emit(NewsResult.Success(article.toArticle()))
                return@flow
            }

            try {
                val remoteArticle : NewsListDto = httpClient.get(baseUrl){
                    parameter("apiKey", apiKey)
                    parameter("id", articleId)
                }.body()

                println(tag + "got remote article remote " + remoteArticle.results?.size)

                if(remoteArticle.results?.isNotEmpty() == true){
                    emit(NewsResult.Success(remoteArticle.results[0].toArticle()))
                }

            }catch (e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw e
                println(tag + "get remote article exception: " + e.message)
                emit(NewsResult.Error("Can't load the article"))
            }
        }

    }
}