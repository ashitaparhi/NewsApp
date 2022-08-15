package com.ashita.news.data.repository

import com.ashita.news.data.remote.models.responses.NewsResponse
import com.ashita.news.data.remote.service.NewsAPI
import com.ashita.news.utils.NetworkUtils
import com.ashita.news.utils.PaginationDataCache
import com.ashita.news.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepository @Inject constructor(private val api: NewsAPI) {

    fun getNews(category: String, pageNo: Int): Flow<Resource<NewsResponse>> {
        return flow {
            if (NetworkUtils.getNetworkStatus()) {
                val response = api.getTopHeadlines(category, pageNo)
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body?.articles != null) {
                        body.articles.forEach {
                            it.category = category
                        }
                        //saving total articles in response to datastore
                        PaginationDataCache().updateTotalArticles(body.totalResults!!)
                        emit(Resource.Success(body))
                    } else {
                        emit(Resource.Error("No response from server"))
                    }
                } else {
                    emit(Resource.Error("Server Error"))
                }
            } else {
                emit(Resource.Error("No internet connection"))
            }
        }
    }
}