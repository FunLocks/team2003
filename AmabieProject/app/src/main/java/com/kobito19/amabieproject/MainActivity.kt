package com.kobito19.amabieproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var text_console = findViewById<TextView>(R.id.text_message)
        var text_share = findViewById<TextView>(R.id.share_button)

        text_share.setOnClickListener{
            text_console.setText(R.string.text_message1)
        }
    }
}