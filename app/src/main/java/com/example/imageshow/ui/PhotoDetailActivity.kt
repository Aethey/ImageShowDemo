package com.example.imageshow.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.imageshow.Config
import com.example.imageshow.databinding.ActivityPhotoDetailBinding

/**
 * Created by Ryu on 17,五月,2021
 */
class PhotoDetailActivity : AppCompatActivity() {
    private val TAG = "PhotoDetailActivity"
    private lateinit var binding: ActivityPhotoDetailBinding
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var thumbnailUrl: String
    private lateinit var fullUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        thumbnailUrl = intent.getStringExtra("smallUrl")
        fullUrl = intent.getStringExtra("fullUrl")


        /*
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(
            binding.imageThumbnail,
            Config.PHOTO_DETAIL_TRANSITION_NAME
        )

        loadImage()
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }

    private fun loadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // at first show Thumbnail
            loadThumbnail()
        } else {
            // Time for a true display of image
            loadFullSizeImage()
        }
    }

    private fun loadThumbnail() {
        Glide.with(this)
            .apply {
                // Set of available caching strategies for image.
                RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            .load(thumbnailUrl)
            .into(binding.imageThumbnail)
    }

    private fun loadFullSizeImage() {
        Glide.with(this)
            .apply {
                // Set of available caching strategies for image.
                RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            .load(fullUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // when imageFull is load over imageFull show and imageThumbnail gone
                    binding.imageFull.visibility = View.VISIBLE
                    binding.imageThumbnail.visibility = View.GONE
                    return false
                }

            })
            .into(binding.imageFull)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            // zoom image
            // just imageFull is zoomable
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = maxOf(0.1f, minOf(scaleFactor, 10.0f))
            binding.imageFull.scaleX = scaleFactor
            binding.imageFull.scaleY = scaleFactor
            return true
        }
    }

    @RequiresApi(21)
    private fun addTransitionListener(): Boolean {
        val transition: Transition = window.sharedElementEnterTransition
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                // load the full-size image
                loadFullSizeImage()
                // remove ourselves as a listener
                transition.removeListener(this)
            }

            override fun onTransitionCancel(p0: Transition?) {
                transition.removeListener(this)
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionResume(p0: Transition?) {
            }
        })
        return true
    }

}