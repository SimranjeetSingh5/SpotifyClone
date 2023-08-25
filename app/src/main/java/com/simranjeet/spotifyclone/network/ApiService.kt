package com.simranjeet.spotifyclone.network

import com.simranjeet.spotifyclone.models.Songs
import com.simranjeet.spotifyclone.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(Constants.GET_SONGS)
    suspend fun getSongs(): Response<Songs>?
}