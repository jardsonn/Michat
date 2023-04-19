package com.jalloft.michat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jalloft.michat.MichatApplication
import com.jalloft.michat.ui.components.NetworkStatus
import com.jalloft.michat.ui.theme.MichatTheme
import com.jalloft.michat.utils.connectivityState
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MichatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val connectionState by connectivityState()

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NetworkStatus(connectionState)
                        MichatApp()
                    }

                }
            }
        }
    }
}
