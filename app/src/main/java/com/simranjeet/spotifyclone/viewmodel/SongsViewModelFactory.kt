package com.simranjeet.spotifyclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simranjeet.spotifyclone.repository.SongsRepository

@Suppress("UNCHECKED_CAST")
class SongsViewModelFactory(private val songsRepository:SongsRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SongsViewModel(songsRepository) as T
    }
}