package com.sk.directudhar.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.util.Random


class Utils(private var context: Context) {


    companion object {
        val BASE_URL ="https://udhaarservice.shopkirana.in/"
        var  CLIENT_CREDENTIALS ="client_credentials"
        var  SECRETKEY ="b02013e9-b92b-4563-a330-aec123bf13d7"
        var  APIKYYE ="e57f97e0-46ea-4be0-9fdf-c92b410cf022"
        var cameraRequest = 1888


        fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}