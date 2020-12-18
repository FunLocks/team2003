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
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import org.altbeacon.beacon.*
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import permissions.dispatcher.NeedsPermission
import java.util.*
import kotlin.math.floor

class MainActivity : AppCompatActivity(),BootstrapNotifier,BeaconConsumer {
    companion object {
        const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 1234
        const val CHANNEL_ID = "666"
        private val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        private val ALTBEACON_FORMAT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
    }
    private lateinit var builder : Notification.Builder
    private lateinit var beacon : Beacon
    private lateinit var beaconManager: BeaconManager
    private var region = Region("all-beacons-region",null,null,null)
    private lateinit var regionBootstrap : RegionBootstrap

    @RequiresApi(Build.VERSION_CODES.Q)
    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        val decorView: View = window.decorView
        var text_share = findViewById<TextView>(R.id.share_button)

        var i = 0
        var gifMovie: Int = R.raw.ldle
        val gifView = findViewById<ImageView>(R.id.gifView)
        val amabieList = listOf(R.raw.ldle, R.raw.walk_l, R.raw.walk_r)
        if(i % 2 == 0/* 怒り */){
            gifMovie = R.raw.angry_2
            i += 1
        } else if( i % 5 == 0 /*スマイル*/){
            gifMovie = R.raw.smile_2
            i += 1
        } else {
            gifMovie = amabieList[(0..2).random()]
            i += 1
        }
        Glide.with(this).load(gifMovie).into(gifView)



        text_share.setOnClickListener{
            share(decorView, "test")
//            val builder = ShareCompat.IntentBuilder.from(this)
//            builder.setText("Test")
//            builder.setSubject("")
//            builder.setType("text/plain")
//            builder.startChooser()
        }
        ///////////////////////////////////////////////////ビーコン
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(ALTBEACON_FORMAT))
        builder = Notification.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentTitle("ビーコン探索").setContentText("ビーコン探索中")
        var intent = Intent(this,MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(
                this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        //beaconManager.disableForegroundServiceScanning()
        beaconManager.enableForegroundServiceScanning(builder.build(),456)
        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 100
        beaconManager.backgroundScanPeriod = 5100
        beaconManager.foregroundBetweenScanPeriod = 100
        beaconManager.foregroundScanPeriod = 5100
        beaconManager.bind(this)
        val pref = getSharedPreferences("Distance",Context.MODE_PRIVATE)
        var uuid : String
        if(pref.getString("UUID","NOTFOUND").equals("NOTFOUND")){
            uuid = UUID.randomUUID().toString()
            getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
                putString("UUID",uuid)
                commit()
            }
        }
        else{
            uuid = pref.getString("UUID","NOTFOUND").toString()
        }

        val calender = Calendar.getInstance()
        val today = calender.get(Calendar.DAY_OF_MONTH)
        if(today!=pref.getInt("today",0)) {
            getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
                putInt("today",today)
                clearUUID()
                commit()
            }
        }


        beacon = Beacon.Builder()
                .setId1(uuid)
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(0))
                .build()

        getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
            putFloat("distance",30.toFloat())
            putInt("surroundings",0)
            commit()
        }


        ///////////////////////////////////////////////////ビーコン
    }

    override fun onStart() {
        super.onStart()
        regionBootstrap = RegionBootstrap(this,region)
        beaconManager.startRangingBeaconsInRegion(region)
        val beaconTransmitter = BeaconTransmitter(applicationContext, BeaconParser().setBeaconLayout(ALTBEACON_FORMAT))
        beaconTransmitter.startAdvertising(beacon)
    }


    //アプリのタスクを切った時に呼ばれる
    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
        regionBootstrap.disable()
    }
    //UUIDをデータに加える。重複があれば加えない。その日の接触者とかに使う。
    fun addUUID(uuid : String){
        val pref = getSharedPreferences("Distance",Context.MODE_PRIVATE)
        val size = pref.getInt("num_of_contact",0)
        for(i in 0..size){
            if(pref.getString("contact$i","").equals(uuid)){
                return
            }
        }
        getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
            Log.d(TAG,"UUIDを追加します")
            putString("contact$size",uuid)
            putInt("num_of_contact",size+1)
            commit()
        }
    }

    //UUIDをデータから削除する。リセットする。
    fun clearUUID(){
        val pref = getSharedPreferences("Distance",Context.MODE_PRIVATE)
        val size = pref.getInt("num_of_contact",0)
        for(i in 0..size){
            getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
                putString("contact$i","")
                commit()
            }
        }
        getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
            putInt("num_of_contact",0)
            commit()
        }
    }

    //領域に出入りした時に呼ばれる。
    override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
        Log.d(TAG,"didD")
    }

    //領域を入ったときに呼ばれる
    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun didEnterRegion(p0: Region?) {
        Log.d(TAG,"didEnter")
        beaconManager.startRangingBeaconsInRegion(region)
    }

    //領域を出たときに呼ばれる
    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun didExitRegion(p0: Region?) {
        Log.d(TAG,"didExit")
        beaconManager.stopRangingBeaconsInRegion(region)
        getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
            putFloat("distance",30.toFloat())
            putInt("surroundings",0)
            commit()
        }
    }


    //領域内にいるときに継続してよばれる。
    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier { beacons, _ ->
            var distance : Float = 30.0.toFloat()
            var cnt :Int = 0
            for(beacon in beacons) {
                if(distance > beacon.distance.toFloat())distance = beacon.distance.toFloat()
                if(beacon.distance<2.0){
                    cnt = cnt + 1
                    addUUID(beacon.id1.toString())

                }
                Log.d(TAG,"距離: "+beacon.distance.toString()+" UUID："+beacon.id1.toString())
            }
            getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
                putFloat("distance",distance)
                putInt("surroundings",cnt)
                commit()
            }
            val pref = getSharedPreferences("Distance",Context.MODE_PRIVATE)
            Log.d(TAG,"今日接触した人数:"+pref.getInt("num_of_contact",0).toString()
                    +" 周囲の人: "+ pref.getInt("surroundings",0)+"最も近い距離: "+
                    pref.getFloat("distance",100.0.toFloat()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission() {
        val permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

            if (backgroundLocationPermissionApproved) {
                // フォアグラウンドとバックグランドのバーミッションがある
            } else {
                // フォアグラウンドのみOKなので、バックグラウンドの許可を求める
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // 位置情報の権限が無いため、許可を求める
            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
            )
        }
    }

    //通知用のチャンネルを作る
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    MainActivity.CHANNEL_ID,
                    "お知らせ",
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "お知らせを通知します。"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
}