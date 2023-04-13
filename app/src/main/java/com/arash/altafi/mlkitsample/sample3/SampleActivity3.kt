package com.arash.altafi.mlkitsample.sample3

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.arash.altafi.mlkitsample.databinding.ActivitySample3Binding
import com.arash.altafi.mlkitsample.sample3.utils.FrameAnalyser
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class SampleActivity3 : AppCompatActivity() {

    private val binding by lazy {
        ActivitySample3Binding.inflate(layoutInflater)
    }
    private lateinit var frameAnalyser: FrameAnalyser
    private lateinit var cameraProviderListenableFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        init()
    }

    private fun init() = binding.apply {
        drawingOverlay.setWillNotDraw(false)
        drawingOverlay.setZOrderOnTop(true)
        frameAnalyser = FrameAnalyser(drawingOverlay)

        if (ActivityCompat.checkSelfPermission(
                this@SampleActivity3,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        } else {
            setupCameraProvider()
        }
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupCameraProvider()
        } else {
            val alertDialog = AlertDialog.Builder(this).apply {
                setTitle("Permissions")
                setMessage("The app requires the camera permission to function.")
                setPositiveButton("GRANT") { dialog, _ ->
                    dialog.dismiss()
                    requestCameraPermission()
                }
                setNegativeButton("CLOSE") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                setCancelable(false)
                create()
            }
            alertDialog.show()
        }
    }

    private fun setupCameraProvider() {
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderListenableFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderListenableFuture.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Log.e("APP", e.message!!)
            } catch (e: InterruptedException) {
                Log.e("APP", e.message!!)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        // Set the resolution which is the closest to the screen size.
        val displayMetrics = resources.displayMetrics
        val screenSize = Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(screenSize)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser)
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

}