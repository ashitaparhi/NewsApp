package com.ashita.news.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.data.remote.models.responses.NewsResponse
import com.ashita.news.data.repository.NewsRepository
import com.ashita.news.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    private val _news: MutableLiveData<Resource<List<Article>>> = MutableLiveData()
    private var allNewsDisplayed = mutableListOf<Article>()
    val news: LiveData<Resource<List<Article>>> = _news

    fun fetchNews(category: String, pageNo: Int, isNew: Boolean) = viewModelScope.launch {
        _news.postValue(Resource.Loading())
        //checking if it is a new call for news or a paginated call
        if (isNew) {
            allNewsDisplayed.clear()
        }
        repository.getNews(category, pageNo)
            .onStart {
                Resource.Loading<NewsResponse>()
            }
            .flowOn(Dispatchers.IO)
            .catch {
                _news.postValue(Resource.Error("No internet connection"))
            }
            .collect {
                if (it.data?.articles != null) {
                    //storing the response received from api for pagination
                    allNewsDisplayed.addAll(it.data.articles)
                    _news.postValue(Resource.Success(allNewsDisplayed.toList()))
                } else {
                    _news.postValue(Resource.Error("No internet connection"))
                }
            }
    }
}
