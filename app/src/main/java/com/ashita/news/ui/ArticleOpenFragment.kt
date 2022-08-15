package com.ashita.news.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ashita.news.R
import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.databinding.FragmentArticleOpenBinding
import com.ashita.news.utils.DateUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ArticleOpenFragment : Fragment() {

    private var binding: FragmentArticleOpenBinding? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity?.supportActionBar?.title = getString(R.string.detail_news)
        // Inflate the layout for this fragment
        binding = FragmentArticleOpenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(false)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing navController
        navController = Navigation.findNavController(view)

        //getting the article passed as argument from other fragment
        val article = arguments?.getSerializable("OpenArticle") as Article
        populateScreen(article)

        binding?.fabShare?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share this article...")
            shareIntent.putExtra(Intent.EXTRA_TEXT, article.url)
            startActivity(shareIntent)
        }
    }

    private fun populateScreen(article: Article) {
        with(article) {
            Glide.with(requireContext()).load(urlToImage)
                .transform(CenterCrop(), RoundedCorners(30)).into(binding!!.newsImage)
            binding?.newsDate?.text = DateUtil.getFormattedDate(publishedAt)
            binding?.newsChannelName?.text = source?.name
            binding?.newsTitle?.text = title
            binding?.newsShortDesc?.text = description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}