package com.simranjeet.spotifyclone.models

import com.google.gson.annotations.SerializedName

data class Songs(
    @SerializedName("data") var songItemData: ArrayList<SongItem>? = null
)