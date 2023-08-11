package com.sk.directudhar.di

import android.util.Log
import com.sk.directudhar.MyApplication
import com.sk.directudhar.utils.Aes256
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.callTimeout(40, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(40, TimeUnit.SECONDS)
        okHttpClient.readTimeout(40, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                if (response.code == 200) {
                    if (!request.url.toString().contains("/token") && !request.url.toString().contains("/Borrower/PanImageUpload")&&
                        !request.url.toString().contains("/api/Borrower/Initiate")&& !request.url.toString().contains("/api/Borrower/GetPersonalInformation")
                    ) {
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put("message", JSONObject(response.body!!.string()))
                            val data = jsonObject.getJSONObject("message").getString("Data")
                            val destr = Aes256.decrypt(
                                data,
                                SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()) + "1201"
                            )
                            printMsg(destr)
                            val contentType = response.body!!.contentType()
                            val responseBody = destr.toResponseBody(contentType)
                            return@addInterceptor response.newBuilder().body(responseBody).build()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                response
            }
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("authorization", "Bearer "+ SharePrefs.getInstance(MyApplication.context!!)!!.getString(SharePrefs.TOKEN))
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        okHttpClient.build()
        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    }

    @Singleton
    @Provides
    fun provideApiClient(retrofit: Retrofit): APIServices {
        return retrofit.create(APIServices::class.java)
    }

    private fun printMsg(msg: String) {
        val chunkCount = msg.length / 4050
        for (i in 0..chunkCount) {
            val max = 4050 * (i + 1)
            if (max >= msg.length) {
                Log.d(" API Response ", msg.substring(4050 * i))
            } else {
                Log.d("API Response ", msg.substring(4050 * i, max))
            }
        }
    }


}