package com.simranjeet.spotifyclone.network

import com.simranjeet.spotifyclone.utils.Constants
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET(Constants.GET_SONGS)
    fun getSongs(): Call<ApiService>?
}