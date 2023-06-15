package com.example.directudharsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sk.directudhar.MainDirectUdharActivity
import com.sk.directudhar.utils.Utils.UtilsObject.BaseUrl

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainDirectUdharActivity.SimpleToast(this,"Hello Bhagwan")

        //MainDirectUdharActivity.callLeadApi(this,"Hello Bhagwan")




     }
}