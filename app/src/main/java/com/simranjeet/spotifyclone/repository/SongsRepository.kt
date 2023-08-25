package com.simranjeet.spotifyclone.repository

import com.simranjeet.spotifyclone.network.RetrofitInstance

class SongsRepository{
    suspend fun getSongs() = RetrofitInstance.api.getSongs()

}