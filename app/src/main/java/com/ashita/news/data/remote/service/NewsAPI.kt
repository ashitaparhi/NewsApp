package com.ashita.news.data.remote.service


import com.ashita.news.data.remote.models.responses.NewsResponse
import com.ashita.news.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category")
        category: String,
        @Query("page")
        page: Int = 1,
        @Query("country")
        countryCode: String = "in",
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>
}