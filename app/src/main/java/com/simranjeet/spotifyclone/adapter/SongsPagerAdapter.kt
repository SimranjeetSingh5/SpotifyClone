package com.simranjeet.spotifyclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.simranjeet.spotifyclone.R
import com.simranjeet.spotifyclone.databinding.CoverSliderItemBinding
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.utils.Constants

class SongsPagerAdapter(var imageUrlList: List<SongItem>) :
    RecyclerView.Adapter<SongsPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(private val binding: CoverSliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUrl: String) {

            Glide.with(binding.root.context)
                .load(Constants.BASE_URL + Constants.GET_COVER + imageUrl)
                .error(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.coverImage)
        }

    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = CoverSliderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.setData(imageUrlList[position].cover!!)
    }
}