package com.example.directudharsdk

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.directudharsdk.Api.CommonMainClassForAPI
import com.sk.directudhar.activity.DirectUdharActivity
import com.sk.directudhar.model.TokenResponse
import com.sk.directudhar.utils.SharePrefs
import io.reactivex.observers.DisposableObserver


class MainActivity : AppCompatActivity() {
    private var mobileNumber:String?="3498529353"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
     }

    private fun initView() {
        CommonMainClassForAPI.getInstance(this).getTokenClient(
            callAppTokenDes, "client_credentials",
            "b02013e9-b92b-4563-a330-aec123bf13d7",
            "e57f97e0-46ea-4be0-9fdf-c92b410cf022"
        )


    }

    private val callAppTokenDes: DisposableObserver<TokenResponse?> =
        object : DisposableObserver<TokenResponse?>() {
            override fun onNext(response: TokenResponse) {
                try {
                    if (response != null) {
                        val intent = Intent(applicationContext, DirectUdharActivity::class.java)
                        SharePrefs.getInstance(applicationContext).putString(SharePrefs.TOKEN,response.access_token)
                        intent.putExtra("token",response.access_token)
                        intent.putExtra("mobileNumber",mobileNumber)
                        startActivityForResult(intent,111)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onComplete() {}
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}