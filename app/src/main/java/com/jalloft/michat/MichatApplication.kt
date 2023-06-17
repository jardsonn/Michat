package com.jalloft.michat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MichatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        openIA = OpenAI(token = BuildConfig.OPENIA_API_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

//        val appComponent = DaggerResetCounterMessagesComponent.builder().build()
//        val receiver = ResetCounterMessagesReceiver()
//        appComponent.inject(receiver)


//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, ResetCounterMessagesReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.add(Calendar.MINUTE, 1)
//
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            60 * 1000, // um minuto
//            pendingIntent
//        )

//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, 0)
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 0)
//        calendar.add(Calendar.DAY_OF_MONTH, 1)
//
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )

    }
}