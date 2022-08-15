package com.ashita.news.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PaginationDataCache {

    private val _totalArticles: MutableLiveData<Int> = MutableLiveData()

    fun updateTotalArticles(total: Int) {
        _totalArticles.postValue(total)
    }

    val totalArticles: LiveData<Int> get() = _totalArticles
}