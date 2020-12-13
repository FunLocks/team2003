package com.kobito19.amabieproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    fun onBackHome(view: View) {
        finish()
    }
}