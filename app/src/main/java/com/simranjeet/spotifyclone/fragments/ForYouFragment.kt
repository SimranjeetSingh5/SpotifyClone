package com.simranjeet.spotifyclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simranjeet.spotifyclone.activities.MainActivity
import com.simranjeet.spotifyclone.adapter.ForYouAdapter
import com.simranjeet.spotifyclone.databinding.FragmentForYouBinding
import com.simranjeet.spotifyclone.listeners.SongsListener
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.utils.Resource
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel

class ForYouFragment : Fragment(), SongsListener {
    private lateinit var binding: FragmentForYouBinding
    private lateinit var viewModel: SongsViewModel
    private lateinit var forYouAdapter: ForYouAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForYouBinding.inflate(layoutInflater)
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
                        forYouAdapter = ForYouAdapter(songsResponse.songItemData?.filter { it.topTrack==false},this)
                        binding.forYouRv.apply {
                            val llm = LinearLayoutManager(context)
                            llm.orientation = LinearLayoutManager.VERTICAL
                            layoutManager = llm
                            adapter = forYouAdapter
                        }
                        Log.e("Response: ", songsResponse.songItemData!!.toString())
                    }
                }


                else -> {}
            }

        }
    }



    override fun onSongClick(song: SongItem) {
        viewModel.currentSong.value = song
    }

}