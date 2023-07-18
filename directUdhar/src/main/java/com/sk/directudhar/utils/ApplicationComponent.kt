package com.sk.directudhar.utils

import com.sk.directudhar.di.NetworkModule
import com.sk.directudhar.ui.MainActivitySDk
import com.sk.directudhar.ui.mainhome.MainFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(mainActivitySDk: MainActivitySDk)

    fun injectFragment(fragment: MainFragment)
}