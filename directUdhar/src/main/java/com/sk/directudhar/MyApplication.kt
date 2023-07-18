package com.sk.directudhar

import android.app.Application
import android.content.Context
import com.sk.directudhar.utils.ApplicationComponent
import com.sk.directudhar.utils.DaggerApplicationComponent

/**
 * Created by Bhagwan on 13/07/2023.
 */
class MyApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent


    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent.builder().build()

    }

    companion object {
        var instance: MyApplication? = null
        var  context: Context? = null
        fun initialize(application: Application){
            context= application
        }
    }

    @Synchronized
    fun getInstance(): MyApplication? {
        return instance
    }






}