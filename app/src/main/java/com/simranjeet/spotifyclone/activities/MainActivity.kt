package com.simranjeet.spotifyclone.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.simranjeet.spotifyclone.adapter.SongsPagerAdapter
import com.simranjeet.spotifyclone.databinding.ActivityMainBinding
import com.simranjeet.spotifyclone.repository.SongsRepository
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel
import com.simranjeet.spotifyclone.viewmodel.SongsViewModelFactory

val songTypeArray = arrayOf(
    "For you",
    "Top Tracks",
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: SongsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository = SongsRepository()
        val viewModelProviderFactory = SongsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[SongsViewModel::class.java]
        viewModel.getSongs()
//        viewModel.songsMutableLiveData.observe(this) { response ->
//            when (response) {
//                is Resource.Success -> {
//                    response.data?.let { songsResponse ->
//                        Log.e("Response: ", songsResponse.accent!!)
//                    }
//                }
//
//                is Resource.Error -> {
//                    response.message?.let { message ->
//                        Log.e("Error Response: ", message)
//                    }
//                }
//
//                else -> {
//                    Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_LONG).show()
//                }
//            }
//
//        }
        val adapter = SongsPagerAdapter(supportFragmentManager, lifecycle)

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = songTypeArray[position]
//            tab.icon =
        }.attach()
    }
}