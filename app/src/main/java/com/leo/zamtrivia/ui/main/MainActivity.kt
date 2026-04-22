package com.leo.zamtrivia.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leo.zamtrivia.databinding.ActivityMainBinding
import com.leo.zamtrivia.util.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}