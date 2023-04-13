package com.arash.altafi.mlkitsample.sample2

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arash.altafi.mlkitsample.databinding.ActivitySample2Binding
import com.arash.altafi.mlkitsample.sample2.utils.camera.CameraManager

class SampleActivity2 : AppCompatActivity() {

    private val binding by lazy {
        ActivitySample2Binding.inflate(layoutInflater)
    }
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        cameraManager = CameraManager(
            this@SampleActivity2,
            viewCameraPreview,
            viewGraphicOverlay,
            this@SampleActivity2
        )
        askCameraPermission()
        buttonClicks()
    }

    private fun buttonClicks() = binding.apply {
        buttonTurnCamera.setOnClickListener {
            cameraManager.changeCamera()
        }
        buttonStopCamera.setOnClickListener {
            cameraManager.cameraStop()
            buttonVisibility(false)
        }
        buttonStartCamera.setOnClickListener {
            cameraManager.cameraStart()
            buttonVisibility(true)
        }
    }

    private fun buttonVisibility(forStart: Boolean) = binding.apply {
        if (forStart) {
            buttonStopCamera.visibility = View.VISIBLE
            buttonStartCamera.visibility = View.INVISIBLE
        } else {
            buttonStopCamera.visibility = View.INVISIBLE
            buttonStartCamera.visibility = View.VISIBLE
        }
    }

    private fun askCameraPermission() {
        if (arrayOf(android.Manifest.permission.CAMERA).all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            cameraManager.cameraStart()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraManager.cameraStart()
        } else {
            Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

}