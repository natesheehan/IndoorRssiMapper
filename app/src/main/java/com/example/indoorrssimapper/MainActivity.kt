package com.example.indoorrssimapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager:WifiManager
    private lateinit var db:FirebaseFirestore

    private val sniff = Runnable {
        wifiManager.connectionInfo.run {
            Log.i("RSSI", "{ssid:${ssid}, rssi : ${rssi}}")
            // Create a new user with a first and last name
            val info = hashMapOf(
                "ssid" to ssid,
                "rssi" to rssi,
                "timestamp" to System.currentTimeMillis()
            )

// Add a new document with a generated ID
            db.collection("wifiInfo")
                .add(info)
                .addOnSuccessListener { documentReference ->
                    Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("db", "Error adding document", e)
                }
        }

        rerun()
    }

    private fun rerun(){
        Handler().postDelayed(sniff, TimeUnit.SECONDS.toMillis(1))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

// Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()


        txt.setOnClickListener{
            Handler().post(sniff)
            Toast.makeText(this,"You are now recording RSSI", Toast.LENGTH_LONG).show()
        }

    }

}
