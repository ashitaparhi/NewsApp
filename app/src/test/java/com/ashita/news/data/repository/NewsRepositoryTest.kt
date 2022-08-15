package com.ashita.news.data.repository

import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.data.remote.models.entities.Source
import com.ashita.news.data.remote.models.responses.NewsResponse
import com.ashita.news.data.remote.service.NewsAPI
import com.ashita.news.utils.NetworkUtils
import com.ashita.news.utils.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response


internal class NewsRepositoryTest {
    private lateinit var newsRepository: NewsRepository
    private val newsAPI = mockk<NewsAPI>()
    private val response: Response<NewsResponse> = mockk(relaxed = true)

    @Before
    fun setUp() {
        newsRepository = NewsRepository(newsAPI)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should verify No internet connection message when there is no internet`() {
        runBlocking {
            val noInternetErrorMessage = "No internet connection"
            var actual: String? = null

            mockkObject(NetworkUtils.Companion)
            every { NetworkUtils.Companion.getNetworkStatus() } returns false

            newsRepository.getNews("General", 1)
                .collect {
                    if (it is Resource.Error) {
                        actual = it.error
                    }
                }
            Assert.assertEquals(noInternetErrorMessage, actual)
        }
    }

    @Test
    fun `should verify server error message when there is any server issue`() {
        runBlocking {
            val serverErrorMessage = "Server Error"
            var actual: String? = null
            mockkObject(NetworkUtils.Companion)
            every { NetworkUtils.Companion.getNetworkStatus() } returns true

            coEvery { newsAPI.getTopHeadlines("General", 1) } returns response
            every { response.isSuccessful } returns false

            newsRepository.getNews("General", 1)
                .collect {
                    if (it is Resource.Error) {
                        actual = it.error
                    }
                }
            Assert.assertEquals(serverErrorMessage, actual)
        }
    }

    @Test
    fun `should verify no response from server message when there is requests are getting timeout or any other reason`() {
        runBlocking {
            val noResponseFromServerMessage = "No response from server"
            var actual: String? = null
            mockkObject(NetworkUtils.Companion)
            every { NetworkUtils.Companion.getNetworkStatus() } returns true

            coEvery { newsAPI.getTopHeadlines("General", 1) } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns null

            newsRepository.getNews("General", 1)
                .collect {
                    if (it is Resource.Error) {
                        actual = it.error
                    }
                }
            Assert.assertEquals(noResponseFromServerMessage, actual)
        }
    }

    @Test
    fun `should validate when receive expected response from server`() {
        runBlocking {
            val author = "NDTV News"
            var actual: String? = null

            val articles = mutableListOf<Article>()
            val article = Article(
                Source("1", "Republic TV"),
                author,
                "title",
                "description",
                "url",
                "urlToImage",
                "publishedAt",
                "content",
                "category"
            )
            articles.add(article)

            val newsResponse = NewsResponse("OK", 1, articles)
            mockkObject(NetworkUtils.Companion)
            every { NetworkUtils.Companion.getNetworkStatus() } returns true

            coEvery { newsAPI.getTopHeadlines("General", 1) } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns newsResponse

            newsRepository.getNews("General", 1)
                .collect {
                    if (it is Resource.Success) {
                        actual = it.data?.articles?.get(0)?.author
                    }
                }
            Assert.assertEquals(author, actual)
        }
    }

}