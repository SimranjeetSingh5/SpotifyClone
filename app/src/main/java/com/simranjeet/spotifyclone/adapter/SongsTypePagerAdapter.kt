package com.simranjeet.spotifyclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.simranjeet.spotifyclone.fragments.ForYouFragment
import com.simranjeet.spotifyclone.fragments.TopTracksFragment

private const val NUM_TABS = 2

class SongsTypePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ForYouFragment()
            1 -> return TopTracksFragment()

        }
        return ForYouFragment()
    }

}