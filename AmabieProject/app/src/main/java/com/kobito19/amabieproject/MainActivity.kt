package com.kobito19.amabieproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ShareCompat
//test

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var text_share = findViewById<TextView>(R.id.share_button)

        text_share.setOnClickListener{
            val builder = ShareCompat.IntentBuilder.from(this)
            builder.setText("Test")
            builder.setSubject("")
            builder.setType("text/plain")
            builder.startChooser()
        }
    }
}