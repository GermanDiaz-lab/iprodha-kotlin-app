package com.example.test2

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.databinding.ActivityDashboardBinding
import android.content.Context
import android.content.res.Configuration
import android.view.Surface



class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientationBasedOnDevice()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val socialCard = binding.socialCard

        socialCard.setOnClickListener {
            startActivity(Intent(this, SocialesMainActivity::class.java))

        }
        // access views using binding

        binding.photoWorksCard.setOnClickListener {
            // handle click
            startActivity(Intent(this, MainActivity::class.java))

        }
        binding.photoConveniosCard.setOnClickListener {
            // handle click
            startActivity(Intent(this, ConvenioActivity::class.java))

        }
        binding.userCard.setOnClickListener {
            // handle click
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
        }
    }

    private fun setRequestedOrientationBasedOnDevice() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val rotation = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        }

        val orientation: Int = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                }
            Configuration.ORIENTATION_LANDSCAPE ->
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        requestedOrientation = orientation
    }
}

