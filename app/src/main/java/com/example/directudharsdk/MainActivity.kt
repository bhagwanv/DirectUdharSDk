package com.example.directudharsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sk.directudhar.MainDirectUdharActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainDirectUdharActivity.SimpleToast(this,"Hello Bhagwan")
        MainDirectUdharActivity.callLeadApi(this,"https://uat.shopkirana.in/api/Udhar/GenerateLead?CustomerId=64373")




     }
}