package com.kobito19.amabieproject

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.math.sqrt

class SettingActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
        var notificationButton = findViewById<Switch>(R.id.notfication_button)
        val centerLatitude = pref.getFloat("latitude", 0f)
        val centerLongitude = pref.getFloat("longitude", 0f)
        val lastLatitude = pref.getFloat("lastLatitude", 0f)
        val lastLongitude = pref.getFloat("lastLongitude", 0f)
        val distance = distanceMeasure(lastLatitude, lastLongitude, centerLatitude, centerLongitude)

        notificationButton.isChecked = pref.getBoolean("notification_Value", false)
        notificationButton.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("my_settings", Context.MODE_PRIVATE).edit().apply {
                if (isChecked) {
                    putBoolean("notification_Value", true)
                    apply()
                    Log.d("switch", "true")
                    if(distance <= 0.001)
                    notificationDiaplay()
                } else {
                    putBoolean("notification_Value", false)
                    apply()
                    Log.d("switch", "false")
                }
            }
        }
        displaySetting()
    }

    private fun displaySetting() {
    val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
    val placeValue = pref.getString("placeValue", "未登録")
    val messageView: TextView = findViewById(R.id.textView)
    messageView.text = placeValue
    // switch debug
        /*
    val switchValue = pref.getBoolean("notification_Value", false)
    val switchText: TextView = findViewById(R.id.switchText)
    switchText.text = switchValue.toString()
    // location debug
    val centerLatitude = pref.getFloat("latitude", 0f)
    val centerlongitude = pref.getFloat("longitude", 0f)
    val centerLatitudeView: TextView = findViewById(R.id.centerlatitude)
    val centerlongitudeView: TextView = findViewById(R.id.centerlongitude)
    centerLatitudeView.text = "緯度$centerLatitude"
    centerlongitudeView.text = "経度$centerlongitude"
    // lastLocationDebug
    Log.d("lastLatitude", pref.getFloat("lastLatitude", 0f).toString())
    Log.d("lastLongitude", pref.getFloat("lastLongitude", 0f).toString())
*/
}


/*
    fun onBackHome(view: View) {
        finish()
    }
*/
    fun clickOnMaps(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        displaySetting()
    }

    fun distanceMeasure(lastLatittude: Float, lastLongtitude: Float, centerLatitude : Float, centerLongtitude: Float): Double {
        return Math.sqrt(((centerLatitude - lastLatittude) * (centerLatitude - lastLatittude) + (centerLongtitude - lastLongtitude) * (centerLongtitude - lastLongtitude)).toDouble())

    }

    fun notificationDiaplay() {

        val CHANNEL_ID = "channel_id"
        val channel_name = "channel_name"
        val channel_description = "channel_description "

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val channelId = "NOTIFICATION_LOCAL"
        val builder = NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle("おかえりなさい！")
            setContentText("手洗いうがいをしましょう！")
            setContentIntent(pendingIntent)
            priority = NotificationCompat.PRIORITY_HIGH
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_name
            val descriptionText = channel_description
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(12345, builder.build())
        }
    }
}