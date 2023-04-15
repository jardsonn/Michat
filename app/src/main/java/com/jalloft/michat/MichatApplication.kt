package com.jalloft.michat

import android.app.Application
import com.aallam.openai.client.OpenAI
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MichatApplication : Application() {

//    lateinit var openIA: OpenAI

    override fun onCreate() {
        super.onCreate()
//        openIA = OpenAI(token = BuildConfig.OPENIA_API_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}