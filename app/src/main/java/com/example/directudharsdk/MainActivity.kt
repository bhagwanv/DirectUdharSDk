package com.example.directudharsdk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sk.directudhar.ui.MainActivitySDk

class MainActivity : AppCompatActivity() {
    lateinit var mobileString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val accountBt = findViewById<Button>(R.id.btAccount)
        val checkoutBt = findViewById<Button>(R.id.btCheckout)
        val mobileNuber = findViewById<EditText>(R.id.etMobileNumber)

        accountBt.setOnClickListener {
            mobileString = mobileNuber.text.toString().trim()
            if (mobileString.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, MainActivitySDk::class.java).putExtra("mobileNumber",mobileString))
            }

        }


    }

}