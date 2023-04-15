package com.jalloft.michat.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Jardson Costa on 15/04/2023.
 */

@Entity(tableName = "assistant")
@Immutable
data class Assistant(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    val name: String,
    val specialty: String,
    @Embedded
    val messages: List<Message>,
)

