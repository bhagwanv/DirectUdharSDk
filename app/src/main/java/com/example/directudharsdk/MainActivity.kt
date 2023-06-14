package com.example.directudharsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sk.directudhar.MainDirectUdharActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


         
        MainDirectUdharActivity.SimpleToast(this,"Hello Bhagwan")


     }
}