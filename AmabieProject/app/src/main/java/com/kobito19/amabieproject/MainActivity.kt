package com.kobito19.amabieproject

import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat

import android.view.MotionEvent

//test

class MainActivity : AppCompatActivity() {

    private var _glSurfaceView: GLSurfaceView? = null
    private var _glRenderer: GLRenderer? = null

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

        //super.onCreate(savedInstanceState)
        JniBridgeJava.SetActivityInstance(this)
        JniBridgeJava.SetContext(this)
        _glSurfaceView = GLSurfaceView(this)
        _glSurfaceView!!.setEGLContextClientVersion(2)
        _glRenderer = GLRenderer()
        _glSurfaceView!!.setRenderer(_glRenderer)
        _glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        setContentView(_glSurfaceView)

//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) View.SYSTEM_UI_FLAG_LOW_PROFILE else View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }
}