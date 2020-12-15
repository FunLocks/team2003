package com.kobito19.amabieproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        /*
        val usertext = intent.getStringExtra("MAP_ACTIVITY")
        val messageView: TextView = findViewById(R.id.textView)
        messageView.text = usertext*/

        displaySetting()

    }
    private fun displaySetting() {
        val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
        val stringValue = pref.getInt("latitude", 0)
        val stringValueI = pref.getInt("longitude", 0)
        val messageView : TextView = findViewById(R.id.textView)
        val messageViewI : TextView = findViewById(R.id.textView2)
        messageView.text = "緯度$stringValue"
        messageViewI.text = "経度$stringValueI"

    }

    fun onBackHome(view: View) {
        finish()
    }

    fun clickOnMaps(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        displaySetting()
    }
}