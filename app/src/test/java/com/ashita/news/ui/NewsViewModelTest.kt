package com.ashita.news.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.data.remote.models.entities.Source
import com.ashita.news.data.remote.models.responses.NewsResponse
import com.ashita.news.data.repository.NewsRepository
import com.ashita.news.utils.Resource
import com.ashita.news.utils.TestUtils
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


internal class NewsViewModelTest {

    private var newsViewModel: NewsViewModel = mockk(relaxed = true)
    private val newsRepository: NewsRepository = mockk(relaxed = true)
    private lateinit var lifecycleOwner: LifecycleOwner

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        lifecycleOwner = TestUtils.mockLifecycleOwner()
        newsViewModel = NewsViewModel(newsRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should verify loading when fetch news request is initiated`() {
        var state: String? = null;
        newsViewModel = NewsViewModel(newsRepository)
        newsViewModel.news.observe(lifecycleOwner) {
            if (it is Resource.Loading) {
                state = "Loading"
            }
        }
        newsViewModel.fetchNews("General", 1, true)
        Assert.assertEquals("Loading", state)
    }

    @Test
    fun `should verify No internet connection message when there is no internet`() = runBlocking {
        val noInternetErrorMessage = "No internet connection"
        var actual: String? = null
        every { newsRepository.getNews("General", 1) } returns flow {
            emit(
                Resource.Error(
                    noInternetErrorMessage
                )
            )
        }
        newsViewModel.fetchNews("General", 1, true)
        newsViewModel.news.observe(lifecycleOwner) {
            if (it is Resource.Error) {
                actual = it.error
            }
        }
        Assert.assertEquals(noInternetErrorMessage, actual)
    }

    @Test
    fun `should verify No internet connection message when received invalid data from server`() =
        runBlocking {
            val noInternetErrorMessage = "No internet connection"
            var actual: String? = null
            val newsResponse = NewsResponse("OK", 1, null)
            every { newsRepository.getNews("General", 1) } returns flow {
                emit(
                    Resource.Success(
                        newsResponse
                    )
                )
            }
            newsViewModel.fetchNews("General", 1, true)
            newsViewModel.news.observe(lifecycleOwner) {
                if (it is Resource.Error) {
                    actual = it.error
                }
            }
            Assert.assertEquals(noInternetErrorMessage, actual)
        }

    @Test
    fun `should verify No internet connection message when received invalid data from server1`() =
        runBlocking {
            var actual: String? = null
            val author = "NDTV News"
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
            every { newsRepository.getNews("General", 1) } returns flow {
                emit(
                    Resource.Success(
                        newsResponse
                    )
                )
            }
            newsViewModel.fetchNews("General", 1, true)
            newsViewModel.news.observe(lifecycleOwner) {
                if (it is Resource.Success) {
                    actual = it.data?.get(0)?.author
                }
            }
            newsRepository.getNews("General", 1)
                .collect {
                    if (it is Resource.Success) {
                        actual = it.data?.articles?.get(0)?.author
                    }
                }
            Assert.assertEquals(author, actual)
        }
}