package com.jalloft.michat.data

/**
 * Created by Jardson Costa on 10/04/2023.
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastMessage(
    val assistant: AssistantIdentifier,
    val message: String
): Parcelable
