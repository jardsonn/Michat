package com.jalloft.michat.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.jalloft.michat.ui.screens.home.getAssistants

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
) {
    @Ignore
    fun getAssistant(): AssistantIdentifier {
        return AssistantIdentifier(assistant = getAssistantById(assistantId))
    }

    @Ignore
    private fun getAssistantById(assistantId: Int): AssistantsEnum {
        return when (assistantId) {
            0 -> AssistantsEnum.FreeChat
            1 -> AssistantsEnum.Galilei
            2 -> AssistantsEnum.Klein
            3 -> AssistantsEnum.Jenner
            4 -> AssistantsEnum.Presley
            5 -> AssistantsEnum.Gauss
            6 -> AssistantsEnum.Ranke
            7 -> AssistantsEnum.Curie
            8 -> AssistantsEnum.Escoffier
            9 -> AssistantsEnum.Dior
            else -> {
                AssistantsEnum.FreeChat
            }
        }
    }
}
