package com.kobito19.amabieproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView

class SettingActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
        var notificationButton = findViewById<Switch>(R.id.notfication_button)
        notificationButton.isChecked = pref.getBoolean("notification_Value", false)
//        notiButton.setSwitchTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC)
        notificationButton.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("my_settings", Context.MODE_PRIVATE).edit().apply {
                if (isChecked) {
                    putBoolean("notification_Value", true)
                    apply()
                    Log.d("switch", "true")
                } else {
                    putBoolean("notification_Value", false)
                    apply()
                    Log.d("switch", "false")
                }
            }
        }
        /*
        val usertext = intent.getStringExtra("MAP_ACTIVITY")
        val messageView: TextView = findViewById(R.id.textView)
        messageView.text = usertext*/

        displaySetting()


    }

    private fun displaySetting() {
        val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
        val placeValue = pref.getString("placeValue", "")
        val messageView : TextView = findViewById(R.id.textView)
        messageView.text = placeValue
        val switchValue = pref.getBoolean("notification_Value", false)
        val switchText : TextView = findViewById(R.id.switchText)
        switchText.text = switchValue.toString()
//        val stringValue = pref.getInt("latitude", 0)
//        val stringValueI = pref.getInt("longitude", 0)
//        val messageView : TextView = findViewById(R.id.textView)
//        val messageViewI : TextView = findViewById(R.id.textView2)
//        messageView.text = "緯度$stringValue"
//        messageViewI.text = "経度$stringValueI"

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