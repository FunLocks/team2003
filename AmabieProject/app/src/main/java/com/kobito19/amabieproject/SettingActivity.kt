package com.kobito19.amabieproject

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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

//    @SuppressLint("SetTextI18n")
    private fun displaySetting() {
    val pref = getSharedPreferences("my_settings", Context.MODE_PRIVATE)
    val placeValue = pref.getString("placeValue", "")
    val messageView: TextView = findViewById(R.id.textView)
    messageView.text = placeValue
    // witch debug
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
//    Log.d("lastLatitude", pref.getFloat("lastLatitude", 0f).toString())
//    Log.d("lastLongitude", pref.getFloat("lastLongitude", 0f).toString())

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

    fun notificationDiaplay() {
        val CHANNEL_ID = "channel_id"
        val channel_name = "channel_name"
        val channel_description = "channel_description "

        ///APIレベルに応じてチャネルを作成
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_name
            val descriptionText = channel_description
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                var description = descriptionText
            }
            /// チャネルを登録
            val notificationManager: NotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        /// 通知の中身
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)    /// 表示されるアイコン
                .setContentTitle("ハローkotlin!!")                  /// 通知タイトル
                .setContentText("今日も1日がんばるぞい!")           /// 通知コンテンツ
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)   /// 通知の優先度

    }

}