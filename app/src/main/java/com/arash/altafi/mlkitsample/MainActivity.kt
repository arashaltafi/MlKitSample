package com.arash.altafi.mlkitsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.mlkitsample.databinding.ActivityMainBinding
import com.arash.altafi.mlkitsample.sample1.SampleActivity1
import com.arash.altafi.mlkitsample.sample2.SampleActivity2
import com.arash.altafi.mlkitsample.sample3.SampleActivity3

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        btnSample1.setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleActivity1::class.java))
        }

        btnSample2.setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleActivity2::class.java))
        }

        btnSample3.setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleActivity3::class.java))
        }
    }

}