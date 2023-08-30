package com.simranjeet.spotifyclone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.models.Songs
import com.simranjeet.spotifyclone.repository.SongsRepository
import com.simranjeet.spotifyclone.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class SongsViewModel(private val songsRepository: SongsRepository) : ViewModel() {

    val songsMutableLiveData: MutableLiveData<Resource<Songs>> = MutableLiveData()
    val currentSong:MutableLiveData<SongItem> = MutableLiveData()
    val isMusicPlaying:MutableLiveData<Boolean> = MutableLiveData()
    private var songsResponse: Songs? = null
    fun getSongs() =
        viewModelScope.launch {
            songsMutableLiveData.postValue(Resource.Loading())
            val response = songsRepository.getSongs()
            songsMutableLiveData.postValue(handleSongsResponse(response))

        }

    private fun handleSongsResponse(response: Response<Songs>?): Resource<Songs> {
        if (response?.isSuccessful == true) {
            response.body().let { resultResponse ->
                songsResponse = resultResponse
                return Resource.Success(songsResponse!!)

            }
        }
        return Resource.Error(response?.message() ?: "Something went wrong!!")
    }
}