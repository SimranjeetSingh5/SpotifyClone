package com.simranjeet.spotifyclone.listeners

import com.simranjeet.spotifyclone.models.SongItem

interface SongsListener {
    fun onSongClick(song:SongItem)
}