package com.kobito19.amabieproject

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    @RequiresApi(Build.VERSION_CODES.Q)
    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        setContentView(R.layout.activity_main)
        createNotificationChannel()
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
        beaconManager.enableForegroundServiceScanning(builder.build(),456)
        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100
        beaconManager.foregroundBetweenScanPeriod = 0
        beaconManager.foregroundScanPeriod = 1100
        beaconManager.bind(this)
        beacon = Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(0))
                .build()


        ///////////////////////////////////////////////////ビーコン
    }

    override fun onStart() {
        super.onStart()
        var regionBootstrap = RegionBootstrap(this,region)
        beaconManager.startRangingBeaconsInRegion(region)
        val beaconTransmitter = BeaconTransmitter(applicationContext, BeaconParser().setBeaconLayout(ALTBEACON_FORMAT))
        beaconTransmitter.startAdvertising(beacon)
    }



    override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
        Log.d(TAG,"didD")
    }

    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun didEnterRegion(p0: Region?) {
        Log.d(TAG,"didEnter")
        beaconManager.startRangingBeaconsInRegion(region)
    }

    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun didExitRegion(p0: Region?) {
        Log.d(TAG,"didExit")
        beaconManager.stopRangingBeaconsInRegion(region)
    }


    @NeedsPermission(Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    override fun onBeaconServiceConnect() {
        Log.d("bicon","conect"  )
        beaconManager.addRangeNotifier { beacons, _ ->
            for(beacon in beacons) {
                val tx= "UUID:${beacon.id1}\n"+"Accuracy（距離）: ${floor(beacon.distance * 100) /100}m\n"
                Log.d(TAG,tx)
                getSharedPreferences("Distance", Context.MODE_PRIVATE).edit().apply {
                    putFloat("distance",beacon.distance.toFloat())
                    commit()
                }

            }
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
}