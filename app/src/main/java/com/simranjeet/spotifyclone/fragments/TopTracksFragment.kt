package com.simranjeet.spotifyclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simranjeet.spotifyclone.activities.MainActivity
import com.simranjeet.spotifyclone.adapter.TopTracksAdapter
import com.simranjeet.spotifyclone.databinding.FragmentTopTracksBinding
import com.simranjeet.spotifyclone.listeners.SongsListener
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.utils.Resource
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel

class TopTracksFragment : Fragment() , SongsListener {

    private lateinit var binding: FragmentTopTracksBinding
    private lateinit var viewModel: SongsViewModel
    private lateinit var topTracksAdapter:TopTracksAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTopTracksBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    private fun setupRecyclerView(){
        viewModel.songsMutableLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { songsResponse ->
                        topTracksAdapter = TopTracksAdapter(songsResponse.songItemData?.filter { it.topTrack==true},this)
                        binding.topTracksRv.apply {
                            val llm = LinearLayoutManager(context)
                            llm.orientation = LinearLayoutManager.VERTICAL
                            layoutManager = llm
                            adapter = topTracksAdapter
                        }
                        Log.e("Response: ", songsResponse.songItemData!!.toString())
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e("Error Response: ", message)
                    }
                }
                is Resource.Loading ->{
                }

                else -> {
                    Toast.makeText(activity, "Something went wrong!!", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    override fun onSongClick(song: SongItem) {
        viewModel.currentSong.value = song
    }

}