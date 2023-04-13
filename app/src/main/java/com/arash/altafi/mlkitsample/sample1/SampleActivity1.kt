package com.arash.altafi.mlkitsample.sample1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import com.arash.altafi.mlkitsample.databinding.ActivitySample1Binding
import com.arash.altafi.mlkitsample.sample1.facedetector.FaceDetector
import com.arash.altafi.mlkitsample.sample1.facedetector.Frame
import com.arash.altafi.mlkitsample.sample1.facedetector.LensFacing
import com.arash.altafi.mlkitsample.utils.serializable
import com.otaliastudios.cameraview.Facing

class SampleActivity1 : AppCompatActivity() {

    private val binding by lazy {
        ActivitySample1Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) = binding.apply {
        val lensFacing =
            savedInstanceState?.serializable(KEY_LENS_FACING) as Facing? ?: Facing.BACK
        setupCamera(lensFacing)
    }

    private fun setupCamera(lensFacing: Facing) = binding.apply {
        val faceDetector = FaceDetector(faceBoundsOverlay)
        viewfinder.facing = lensFacing
        viewfinder.addFrameProcessor {
            faceDetector.process(
                Frame(
                    data = it.data,
                    rotation = it.rotation,
                    size = Size(it.size.width, it.size.height),
                    format = it.format,
                    lensFacing = if (viewfinder.facing == Facing.BACK) LensFacing.BACK else LensFacing.FRONT
                )
            )
        }

        toggleCameraButton.setOnClickListener {
            viewfinder.toggleFacing()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.viewfinder.start()
    }

    override fun onPause() {
        super.onPause()
        binding.viewfinder.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(KEY_LENS_FACING, binding.viewfinder.facing)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewfinder.destroy()
    }

    companion object {
        private const val KEY_LENS_FACING = "key-lens-facing"
    }

}