package com.ashita.news.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ashita.news.R
import com.ashita.news.data.remote.models.entities.Article
import com.ashita.news.utils.DateUtil
import com.ashita.news.utils.EspressoIdleResource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class NewsRowItemAdapter(private val listener: INewsRVAdapter) :
    ListAdapter<Article, NewsRowItemAdapter.NewsRowItemViewHolder>(NewsDiffUtil()) {

    class NewsDiffUtil : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    fun updateNewsList(list: List<Article>?) {
        EspressoIdleResource.increment()
        val dataCommitCallback = Runnable {
            EspressoIdleResource.decrement()
        }
        submitList(list, dataCommitCallback)
    }

    inner class NewsRowItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsShortDesc)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val name: TextView = itemView.findViewById(R.id.newsChannelName)
        val date: TextView = itemView.findViewById(R.id.newsDate)
        val root: ConstraintLayout = itemView.findViewById(R.id.llNewsArticleRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRowItemViewHolder {
        return NewsRowItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_category_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsRowItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        with(currentItem) {
            holder.title.text = title
            holder.description.text = description
            holder.date.text = DateUtil.getFormattedDate(publishedAt)
            source?.name.also { holder.name.text = it }
            Glide.with(holder.itemView.context).load(currentItem.urlToImage)
                .transform(CenterCrop(), RoundedCorners(30)).into(holder.image)
        }

        holder.root.setOnClickListener {
            listener.onArticleClicked(currentItem)
        }
    }
}

interface INewsRVAdapter {
    fun onArticleClicked(article: Article)
}