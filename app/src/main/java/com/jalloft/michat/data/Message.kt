package com.jalloft.michat.data

/**
 * Created by Jardson Costa on 14/04/2023.
 */


import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.*


@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Assistant::class,
        parentColumns = ["id"],
        childColumns = ["id_assistant"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE,
    )]
)
@Immutable
data class Message(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(index = true, name = "id_assistant") val idAssistant: Int
)
