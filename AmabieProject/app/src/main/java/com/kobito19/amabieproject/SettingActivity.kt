package com.kobito19.amabieproject

import android.content.Intent
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

    fun clickOnMaps(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}