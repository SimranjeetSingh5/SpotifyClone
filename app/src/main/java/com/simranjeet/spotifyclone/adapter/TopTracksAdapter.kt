package com.simranjeet.spotifyclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.simranjeet.spotifyclone.R
import com.simranjeet.spotifyclone.databinding.ItemSongBinding
import com.simranjeet.spotifyclone.listeners.SongsListener
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.utils.Constants

class TopTracksAdapter(private var songs: List<SongItem>?, private val songsListener: SongsListener):
    RecyclerView.Adapter<TopTracksAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTracksAdapter.ViewHolder {

        val binding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myOptions = RequestOptions()
            .centerCrop()
            .override(100, 100)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        with(holder) {
            with(songs?.get(position)) {
                binding.root.setOnClickListener {
                    songsListener.onSongClick(songs!!.elementAt(position))
                }
                binding.song.text = this?.name
                binding.artist.text = this?.artist
                Glide.with(binding.coverImage.context)
                    .load(Constants.BASE_URL + Constants.GET_COVER + this?.cover)
                    .apply(myOptions)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.coverImage)
            }
        }

    }

    override fun getItemCount(): Int {
        return songs!!.size
    }
}