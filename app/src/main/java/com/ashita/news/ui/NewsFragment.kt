package com.ashita.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashita.news.R
import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.databinding.FragmentNewsBinding
import com.ashita.news.ui.adapter.ICategoryRVAdapter
import com.ashita.news.ui.adapter.INewsRVAdapter
import com.ashita.news.ui.adapter.NewsCategoryTabsAdapter
import com.ashita.news.ui.adapter.NewsRowItemAdapter
import com.ashita.news.utils.Constants
import com.ashita.news.utils.EspressoIdleResource
import com.ashita.news.utils.PaginationDataCache
import com.ashita.news.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment(), ICategoryRVAdapter, INewsRVAdapter {

    private var binding: FragmentNewsBinding? = null
    private lateinit var navController: NavController
    private val newsAdapter = NewsRowItemAdapter(this)
    private lateinit var viewModel: NewsViewModel
    private val categories =
        listOf("General", "Business", "Entertainment", "Sports", "Health", "Science", "Technology")
    private val categoryAdapter = NewsCategoryTabsAdapter(categories, this)
    private var currentCategory = "General"
    private var currentPage = 1
    private var itemsDisplayed = 0
    private var totalArticles: Int? = null
    private var isScrolling: Boolean = false
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as? MainActivity
        activity?.supportActionBar?.title = getString(R.string.app_name)
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        setUpCategoriesRecyclerView()

        setUpArticlesRecyclerView()
        EspressoIdleResource.increment()
        viewModel.fetchNews(currentCategory, 1, true)

        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    newsAdapter.updateNewsList(response.data)
                    isLoading = false
                    PaginationDataCache().totalArticles.observe(viewLifecycleOwner) {
                        totalArticles = it
                    }
                    if (totalArticles != null) {
                        isLastPage = itemsDisplayed + Constants.PAGE_SIZE_QUERY >= totalArticles!!
                    }
                    itemsDisplayed += Constants.PAGE_SIZE_QUERY
                    EspressoIdleResource.decrement()
                }
                is Resource.Error -> {
                    binding?.progressBar?.visibility = View.GONE
                    Snackbar.make(binding?.root!!, "${response.error}", Snackbar.LENGTH_SHORT)
                        .show()
                    EspressoIdleResource.decrement()
                }
                is Resource.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }
        })
    }

    private val paginationScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemsCount = layoutManager.itemCount
            val isNotAtLastPageAndNotLoading = !isLastPage && !isLoading
            val isAtLastPage = firstVisibleItemPosition + visibleItemCount >= totalItemsCount
            val notAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemsCount >= Constants.PAGE_SIZE_QUERY
            val shouldPaginate =
                isNotAtLastPageAndNotLoading && isAtLastPage && isTotalMoreThanVisible && notAtBeginning && isScrolling
            if (shouldPaginate) {
                currentPage += 1
                viewModel.fetchNews(currentCategory, currentPage, false)
                isScrolling = false
            }
        }
    }

    private fun setUpCategoriesRecyclerView() {
        binding?.rvCategory?.apply {
            adapter = categoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpArticlesRecyclerView() {
        binding?.rvNews?.apply {
            adapter = newsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addOnScrollListener(paginationScrollListener)
        }
    }

    override fun onCategoryClicked(category: String) {
        currentCategory = category
        itemsDisplayed = 0
        currentPage = 1
        viewModel.fetchNews(category, 1, true)
    }

    override fun onArticleClicked(article: Article) {
        val bundle = bundleOf("OpenArticle" to article)
        navController.navigate(R.id.action_newsFragment_to_articleOpenFragment, bundle)
    }
}