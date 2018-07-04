package com.uapps.uticket.untels

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button

class ErrorConnection : AppCompatActivity() {

    private lateinit var bt1 : Button
    private lateinit var bt2 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_connection)

        val toolbar : Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null

        bt1 = findViewById<Button>(R.id.reintentar)
        bt2 = findViewById<Button>(R.id.salir)

        bt1.setOnClickListener(View.OnClickListener {
            val reintento = Intent(applicationContext, SplashActivity::class.java)
            startActivity(reintento)
            finish()
        })

        bt2.setOnClickListener(View.OnClickListener {
            finish()
            val salir = Intent(Intent.ACTION_MAIN)
            salir.addCategory(Intent.CATEGORY_HOME)
            salir.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(salir)
        })


    }
}
