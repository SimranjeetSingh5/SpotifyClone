package com.simranjeet.spotifyclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.simranjeet.spotifyclone.activities.MainActivity
import com.simranjeet.spotifyclone.databinding.FragmentForYouBinding
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel

class ForYouFragment : Fragment() {
    private lateinit var binding: FragmentForYouBinding
    lateinit var viewModel: SongsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForYouBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}