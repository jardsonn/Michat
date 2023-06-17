package com.jalloft.michat.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jalloft.michat.repository.FirebaseRepository
import dagger.Component
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.Forest.i
import javax.inject.Inject

@AndroidEntryPoint
class ResetCounterMessagesReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: FirebaseRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        i("ResetCounterMessagesReceiver RESETANDO O TEMPO")
        repo.resetCounterMessages()
    }

}
