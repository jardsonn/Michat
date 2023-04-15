package com.jalloft.michat.data

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.jalloft.michat.data.Assistant
import com.jalloft.michat.data.Message

/**
 * Created by Jardson Costa on 15/04/2023.
 */
data class AssistantWithMessage(
    @Embedded val assistant: Assistant,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_assistant"
    )
    val messages: List<Message>
)