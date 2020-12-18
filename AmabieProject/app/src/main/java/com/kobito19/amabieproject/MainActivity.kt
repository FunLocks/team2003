package com.kobito19.amabieproject


import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.ImageButton


class MainActivity : AppCompatActivity() {

    var serifBox = listOf("おはよう", "こんにちは", "おやすみ", "やれんのか小人共2020", "FunLocks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val decorView: View = window.decorView
        var text_share = findViewById<ImageButton>(R.id.share_button)


        text_share.setOnClickListener{
            share(decorView, "test")
//            val builder = ShareCompat.IntentBuilder.from(this)
//            builder.setText("Test")
//            builder.setSubject("")
//            builder.setType("text/plain")
//            builder.startChooser()
        }
    }
    // シェア用のUriを取得
    private fun Activity.getViewCacheContentUri(view: View): Uri {
        view.isDrawingCacheEnabled = true
        view.destroyDrawingCache()
        val cache = view.drawingCache
        val bitmap = Bitmap.createBitmap(cache)
        view.isDrawingCacheEnabled = false
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val filePath = File(cachePath, "share.png")
        val fos = FileOutputStream(filePath.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", filePath)
    }

    // シェア
    fun Activity.share(view: View, shareText: String) {
        val contentUri = getViewCacheContentUri(view)
        startActivity(Intent().apply {
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(contentUri, contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        })
    }


    fun onClickSetting(view: View) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    fun chengText(view: View) {
        val serif = findViewById<TextView>(R.id.selif_text)
        var randomText = serifBox[(0..4).random()]
        serif.text = randomText

    }
}