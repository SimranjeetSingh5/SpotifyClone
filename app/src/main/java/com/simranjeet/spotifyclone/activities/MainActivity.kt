package com.simranjeet.spotifyclone.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.simranjeet.spotifyclone.databinding.ActivityMainBinding
import com.simranjeet.spotifyclone.repository.SongsRepository
import com.simranjeet.spotifyclone.utils.Resource
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel
import com.simranjeet.spotifyclone.viewmodel.SongsViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: SongsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = SongsRepository()
        val viewModelProviderFactory = SongsViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[SongsViewModel::class.java]
        viewModel.getSongs()
        viewModel.songsMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { songsResponse ->
                        Log.e("Response: ", songsResponse.accent!!)
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e("Error Response: ", message)
                    }
                }

                else -> {
                    Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_LONG).show()
                }
            }

        }
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//        ))
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }
}