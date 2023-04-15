package com.jalloft.michat.utils

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Jardson Costa on 15/04/2023.
 */

@Entity(tableName = "messages")
@Immutable
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val timestamp: Long,
    val role: String,
    @ColumnInfo(name = "assistant_id") val assistantId: Int
)
