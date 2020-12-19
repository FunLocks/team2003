package com.kobito19.amabieproject

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FirstLaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_launch)
        var gifMovie: Int = R.raw.ldle
        val gifView = findViewById<ImageView>(R.id.gifView)
        Glide.with(this).load(gifMovie).into(gifView)
        var returnButtun = findViewById<Button>(R.id.button)
        returnButtun.setBackgroundColor(Color.alpha(0))
        returnButtun.setOnClickListener{
            finish()
        }
    }
}
