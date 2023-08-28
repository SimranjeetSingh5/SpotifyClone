package com.simranjeet.spotifyclone.activities

import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.MarginPageTransformer
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


val songTypeArray = arrayOf(
    "For you",
    "Top Tracks",
)

/**
 * TODO->
 * 1.Network connection check validation
 * 2.Tab view dot indicator
 * 3.Tab view background fade
 **/
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: SongsViewModel
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var songsPagerAdapter: SongsPagerAdapter
    var mediaPlayer: MediaPlayer? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mBottomSheetBinding: MusicPlayerLayoutBinding
    val myOptions = RequestOptions()
        .centerCrop()
        .override(100, 100)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository = SongsRepository()
        val viewModelProviderFactory = SongsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[SongsViewModel::class.java]
        viewModel.getSongs()
        initializeViews()
        viewModel.songsMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    songsPagerAdapter = SongsPagerAdapter(response.data?.songItemData!!)
                    mBottomSheetBinding.songCoverViewPager.adapter = songsPagerAdapter}
                else -> {
                    Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_LONG).show()
                }}
        }
        viewModel.currentSong.observe(this) { songItem ->
            binding.miniPlayerLayout.visibility = View.VISIBLE
            binding.miniPlayerLayout.setOnClickListener {
                mBottomSheetBinding.mainMusicCl.minHeight = Resources.getSystem().displayMetrics.heightPixels
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED;
                bottomSheetDialog.show()
                mediaPlayer?.start()
            }
            val currentSongsIndex =
                viewModel.songsMutableLiveData.value!!.data?.songItemData?.indexOf(songItem)!!
            val currentSongsDetails =
                viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(
                    currentSongsIndex
                )
             if (currentSongsIndex< viewModel.songsMutableLiveData.value!!.data?.songItemData?.size!!)
                 mBottomSheetBinding.songCoverViewPager.currentItem = currentSongsIndex
            updatePlayerView(currentSongsDetails)
            mBottomSheetBinding.playPauseButton.setOnClickListener {
                if (mediaPlayer?.isPlaying == true) {
                    it.setBackgroundResource(R.drawable.ic_play)
                    mediaPlayer?.pause()
                } else {
                    it.setBackgroundResource(R.drawable.ic_pause)
                    mediaPlayer?.start()
                }
            }
            mediaPlayer?.setOnCompletionListener { mBottomSheetBinding.next.performClick() }
            mBottomSheetBinding.next.setOnClickListener {
                if (currentSongsIndex != viewModel.songsMutableLiveData.value!!.data?.songItemData?.size)
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(
                            currentSongsIndex + 1
                        )
            }
            mBottomSheetBinding.previous.setOnClickListener {
                if (currentSongsIndex > 0)
                    viewModel.currentSong.value =
                        viewModel.songsMutableLiveData.value!!.data?.songItemData?.get(
                            currentSongsIndex - 1
                        )
            }

//                  try {
//                      mediaPlayer.setDataSource(currentSongsDetails?.url)
//                      mediaPlayer.prepare()
//                      mediaPlayer.start()
//                  } catch (e: IOException) {
//                      e.printStackTrace()
//                  }

//                  val updateseekbar = object : Thread() {
//                      override fun run() {
//                          val totalDuration: Int = mediaPlayer.duration
//                          var currentposition = 0
//                          while (currentposition < totalDuration) {
//                              try {
//                                  sleep(500)
//                                  currentposition = mediaPlayer.currentPosition
//                                  mBottomSheetBinding.seekBar.progress = currentposition
//                              } catch (e: InterruptedException) {
//                                  e.printStackTrace()
//                              } catch (e: IllegalStateException) {
//                                  e.printStackTrace()
//                              }
//                          }
//                      }
//                  }
//                  mBottomSheetBinding.seekBar.max = mediaPlayer.duration
//                  updateseekbar.start()
//                  mBottomSheetBinding.seekBar.progressDrawable.setColorFilter(
//                      Color.WHITE,
//                      PorterDuff.Mode.MULTIPLY
//                  )
//                  mBottomSheetBinding.seekBar.thumb.setColorFilter(
//                      Color.TRANSPARENT,
//                      PorterDuff.Mode.SRC_IN
//                  )
//
//                  mBottomSheetBinding.seekBar.setOnSeekBarChangeListener(object :
//                      OnSeekBarChangeListener {
//                      override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
//                      override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                      override fun onStopTrackingTouch(seekBar: SeekBar) {
//                          mediaPlayer.seekTo(seekBar.progress)
//                      }
//                  })
//
//                  val endTime: String = createTime(mediaPlayer.duration)!!
//                  mBottomSheetBinding.totalDuration.text = endTime
//
//                  val handler = Handler()
//                  val delay = 1000
//
//                  handler.postDelayed(object : Runnable {
//                      override fun run() {
//                          val currentTime: String = createTime(mediaPlayer.currentPosition)
//                          mBottomSheetBinding.currentDuration.text = currentTime
//                          handler.postDelayed(this, delay.toLong())
//                      }
//                  }, delay.toLong())
//
//
//
//
//                  mediaPlayer.setOnCompletionListener { mBottomSheetBinding.next.performClick() }

//                val audiosessionId: Int = mediaPlayer.audioSessionId
//                if (audiosessionId != -1) {
//                    visualizer.setAudioSessionId(audiosessionId)
//                }

//                mBottomSheetBinding.next.setOnClickListener{
//                    mediaPlayer.stop()
//                    mediaPlayer.release()
//                    position = (position + 1) % mySongs.size()
//                    val u = Uri.parse(currentSongsDetails?.url)
//                    mediaPlayer = MediaPlayer.create(applicationContext, u)
//                    sname = mySongs.get(position).getName()
//                    txtsname.setText(sname)
//                    mediaPlayer.start()
//                    mBottomSheetBinding.playPauseButton.setBackgroundResource(com.simranjeet.spotifyclone.R.drawable.ic_pause)
//                    startAnimation(imageView)
//                    val audiosessionId: Int = mediaPlayer.getAudioSessionId()
//                    if (audiosessionId != -1) {
//                        visualizer.setAudioSessionId(audiosessionId)
//                    }
//                }
//
//                mBottomSheetBinding.previous.setOnClickListener{
//                    mediaPlayer.stop()
//                    mediaPlayer.release()
//                    position = if (position - 1 < 0) mySongs.size() - 1 else position - 1
//                    val u = Uri.parse(mySongs.get(position).toString())
//                    mediaPlayer = MediaPlayer.create(applicationContext, u)
//                    sname = mySongs.get(position).getName()
//                    txtsname.setText(sname)
//                    mediaPlayer.start()
//                    btnplay.setBackgroundResource(R.drawable.ic_pause)
//                    startAnimation(imageView)
//                    val audiosessionId: Int = mediaPlayer.getAudioSessionId()
//                    if (audiosessionId != -1) {
//                        visualizer.setAudioSessionId(audiosessionId)
//                    }
//                }


        }


//        connectionLiveData = ConnectionLiveData(this)
//        connectionLiveData.observe(this) { isNetworkAvailable ->
//            isNetworkAvailable?.let {
//                binding.viewPager.visibility = View.VISIBLE
//                binding.tabLayout.visibility = View.VISIBLE
//                binding.noInternetTv.visibility = View.GONE
//            }
//            if (!isNetworkAvailable){
//                binding.viewPager.visibility = View.GONE
//                binding.tabLayout.visibility = View.GONE
//                binding.noInternetTv.visibility = View.VISIBLE
//            }
//        }
//        viewModel.songsMutableLiveData.observe(this) { response ->
//            when (response) {
//                is Resource.Success -> {
//                    response.data?.let { songsResponse ->
//                        Log.e("Response: ", songsResponse.accent!!)
//                    }
//                }
//
//                is Resource.Error -> {
//                    response.message?.let { message ->
//                        Log.e("Error Response: ", message)
//                    }
//                }
//
//                else -> {
//                    Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_LONG).show()
//                }
//            }
//
//        }

    }

    private fun updatePlayerView(currentSongsDetails: SongItem?) {
        Glide.with(this)
            .load(Constants.BASE_URL + Constants.GET_COVER + currentSongsDetails?.cover)
            .apply(myOptions)
            .placeholder(R.drawable.placeholder)
            .into(binding.coverImage)
        binding.song.text = currentSongsDetails?.name
        mBottomSheetBinding.song.text = currentSongsDetails?.name
        mBottomSheetBinding.artist.text = currentSongsDetails?.artist
    }

    private fun initializeViews() {
        mediaPlayer = MediaPlayer()
        val adapter = SongsTypePagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = songTypeArray[position]
        }.attach()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        bottomSheetDialog =
            BottomSheetDialog(this, com.simranjeet.spotifyclone.R.style.FullScreenDialog)

        mBottomSheetBinding = MusicPlayerLayoutBinding.inflate(layoutInflater, null, false)

        bottomSheetDialog.setContentView(mBottomSheetBinding.root)


        mBottomSheetBinding.songCoverViewPager.setPageTransformer(MarginPageTransformer(20))

        mBottomSheetBinding.songCoverViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        mBottomSheetBinding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * 1000);
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })


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

    fun playMusicFromUrl(url: String) {
        mediaPlayer?.let {
            it.reset()
            it.setDataSource(url)
            it.prepareAsync()
            it.setOnPreparedListener { player ->
                player.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    fun createTime(duration: Int): String {
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