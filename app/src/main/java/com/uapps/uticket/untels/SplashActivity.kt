package com.uapps.uticket.untels

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.TimerTask
import java.util.Timer

class SplashActivity : AppCompatActivity() {

    lateinit var connectivity : ConnectivityManager
    private val SPLASH_SCREEN_DELAY: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //val activeNetwork : NetworkInfo = connectivity.getActiveNetworkInfo()
        val wifi : NetworkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val datos : NetworkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        val timerTask = object : TimerTask() {
            override fun run() {
                if ((datos != null) and datos.isConnectedOrConnecting || (wifi != null) and wifi.isConnectedOrConnecting) {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }else {
                    val error = Intent(this@SplashActivity, ErrorConnection::class.java)
                    startActivity(error)
                    finish()
                }
            }
        }
        val timer = Timer()
        timer.schedule(timerTask, SPLASH_SCREEN_DELAY)

    }

    override fun onBackPressed() {

    }

}

