package com.simranjeet.spotifyclone.activities

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.simranjeet.spotifyclone.R
import com.simranjeet.spotifyclone.adapter.SongsPagerAdapter
import com.simranjeet.spotifyclone.adapter.SongsTypePagerAdapter
import com.simranjeet.spotifyclone.databinding.ActivityMainBinding
import com.simranjeet.spotifyclone.databinding.MusicPlayerLayoutBinding
import com.simranjeet.spotifyclone.models.SongItem
import com.simranjeet.spotifyclone.repository.SongsRepository
import com.simranjeet.spotifyclone.utils.ConnectionLiveData
import com.simranjeet.spotifyclone.utils.Constants
import com.simranjeet.spotifyclone.utils.Resource
import com.simranjeet.spotifyclone.viewmodel.SongsViewModel
import com.simranjeet.spotifyclone.viewmodel.SongsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val songTypeArray = arrayOf(
    "For you",
    "Top Tracks",
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: SongsViewModel
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var songsPagerAdapter: SongsPagerAdapter
    var mediaPlayer: MediaPlayer? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mBottomSheetBinding: MusicPlayerLayoutBinding
    private var updateJob: Job? = null
    private val myOptions = RequestOptions()
        .centerCrop()
        .override(100, 100)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository = SongsRepository()
        val viewModelProviderFactory = SongsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[SongsViewModel::class.java]
        setupNetworkLivedata()
        initializeViews()
        setUpOnClickListeners()
        setupMusicPlayingObserver()
        setupSongsChangeObserver()
        setupSongsResponseObserver()
    }

    private fun setupSongsResponseObserver() {
        viewModel.songsMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    songsPagerAdapter = SongsPagerAdapter(response.data?.songItemData!!)
                    mBottomSheetBinding.songCoverViewPager.adapter = songsPagerAdapter
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
    }

    private fun setupNetworkLivedata() {

        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this) { isNetworkAvailable ->
            isNetworkAvailable?.let {
                binding.viewPager.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
                binding.noInternetTv.visibility = View.GONE
                viewModel.getSongs()
            }
            if (isNetworkAvailable==null) {
                binding.viewPager.visibility = View.GONE
                binding.tabLayout.visibility = View.GONE
                binding.noInternetTv.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSongsChangeObserver() {
        viewModel.currentSong.observe(this) { songItem ->
            binding.miniPlayerLayout.visibility = View.VISIBLE
            disablePlayPause()
            playMusicFromUrl(songItem?.url!!)
            val currentSongsIndex =
                viewModel.songsMutableLiveData.value!!.data?.songItemData?.indexOf(songItem)!!
            val currentSongsDetails =
                viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(currentSongsIndex)
            if (currentSongsIndex < viewModel.songsMutableLiveData.value!!.data?.songItemData?.size!!) {
                mBottomSheetBinding.songCoverViewPager.currentItem = currentSongsIndex
            }
            updatePlayerView(currentSongsDetails)
            mBottomSheetBinding.next.setOnClickListener {
                if (currentSongsIndex < viewModel.songsMutableLiveData.value!!.data?.songItemData?.size!!-1) {
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(
                            currentSongsIndex + 1
                        )
                }
            }
            mBottomSheetBinding.previous.setOnClickListener {
                if (currentSongsIndex > 0) {
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(
                            currentSongsIndex - 1
                        )
                } else {
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value!!.data?.songItemData?.lastOrNull()
                }
            }
        }
    }
    private fun enablePlayPause(){
        binding.playPauseButton.isEnabled = true
        mBottomSheetBinding.playPauseButton.isEnabled = true
    }
    private fun disablePlayPause(){

        mBottomSheetBinding.playPauseButton.isEnabled = false
        binding.playPauseButton.isEnabled = false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupMusicPlayingObserver() {
        viewModel.isMusicPlaying.observe(this) {
            if (it == true) {
                binding.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_pause,
                        theme
                    )
                )
                mBottomSheetBinding.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_pause,
                        theme
                    )
                )
                mBottomSheetBinding.playPauseButton.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
                binding.playPauseButton.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
                mediaPlayer?.start()
                enablePlayPause()
                setupSeekbar()
            } else {
                binding.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_play,
                        theme
                    )
                )
                binding.playPauseButton.performHapticFeedback(
                    HapticFeedbackConstants.GESTURE_END
                )
                mBottomSheetBinding.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_play,
                        theme
                    )
                )
                mBottomSheetBinding.playPauseButton.performHapticFeedback(
                    HapticFeedbackConstants.GESTURE_END
                )
                enablePlayPause()
                mediaPlayer?.pause()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpOnClickListeners() {
        binding.miniPlayerLayout.setOnClickListener {
            mBottomSheetBinding.mainMusicCl.minHeight =
                Resources.getSystem().displayMetrics.heightPixels
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()
        }
        binding.playPauseButton.setOnClickListener {
            viewModel.isMusicPlaying.value = viewModel.isMusicPlaying.value != true
        }
        mBottomSheetBinding.playPauseButton.setOnClickListener {
            viewModel.isMusicPlaying.value = viewModel.isMusicPlaying.value != true
        }
        mediaPlayer?.setOnCompletionListener { mBottomSheetBinding.next.performClick() }

    }


    private fun updatePlayerView(currentSongsDetails: SongItem?) {
        Glide.with(this)
            .load(Constants.BASE_URL + Constants.GET_COVER + currentSongsDetails?.cover)
            .apply(myOptions)
            .placeholder(R.drawable.placeholder)
            .into(binding.coverImage)

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(currentSongsDetails?.accent), Color.BLACK)
        )
        gradientDrawable.cornerRadius = 0f
        mBottomSheetBinding.root.background = gradientDrawable
        binding.miniPlayerLayout.setBackgroundColor(Color.parseColor(currentSongsDetails?.accent))
        binding.song.text = currentSongsDetails?.name
        mBottomSheetBinding.song.text = currentSongsDetails?.name
        mBottomSheetBinding.artist.text = currentSongsDetails?.artist
    }

    private fun initializeViews() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
        val adapter = SongsTypePagerAdapter(supportFragmentManager, lifecycle)

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = songTypeArray[position]
        }.attach()

        bottomSheetDialog = BottomSheetDialog(this, R.style.FullScreenDialog)
        mBottomSheetBinding = MusicPlayerLayoutBinding.inflate(layoutInflater, null, false)
        bottomSheetDialog.setContentView(mBottomSheetBinding.root)
        mBottomSheetBinding.songCoverViewPager.offscreenPageLimit = 3

        mBottomSheetBinding.songCoverViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        mBottomSheetBinding.songCoverViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value?.data!!.songItemData?.get(position)
                }
            }
        )
    }
    private fun playMusicFromUrl(url: String) {
        try {
            mediaPlayer?.let {
                it.reset()
                it.setDataSource(url)
                it.prepareAsync()
                it.setOnPreparedListener {
                    enablePlayPause()
                    viewModel.isMusicPlaying.value = true
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSeekbar() {
        mBottomSheetBinding.seekBar.max = mediaPlayer?.duration!!
        mBottomSheetBinding.totalDuration.text = createTime(mediaPlayer?.duration!!.toInt())
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer?.isPlaying == true) {
                mBottomSheetBinding.seekBar.progress = mediaPlayer?.currentPosition!!
                mBottomSheetBinding.currentDuration.text =
                    createTime(mediaPlayer?.currentPosition!!)
                delay(1000)
            }
        }
        val seekBarListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    mBottomSheetBinding.currentDuration.text =
                        createTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        mBottomSheetBinding.seekBar.setOnSeekBarChangeListener(seekBarListener)

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        updateJob?.cancel()
    }

    private fun createTime(duration: Int): String {
        var time: String? = ""
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60
        time += "$min:"
        if (sec < 10) {
            time += "0"
        }
        time += sec
        return time!!
    }
}