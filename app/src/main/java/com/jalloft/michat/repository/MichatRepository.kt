package com.jalloft.michat.repository

import androidx.room.Query
import androidx.room.Transaction
import com.jalloft.michat.data.Assistant
import com.jalloft.michat.data.AssistantWithMessage
import com.jalloft.michat.data.Message
import kotlinx.coroutines.flow.Flow


interface MichatRepository {

    fun getMessageWithAssistant(): Flow<List<AssistantWithMessage>>

    fun getAssistants(): Flow<List<Assistant>>

    fun getMessagesByAssistant(idAssistant: Int): Flow<List<Message>>

    fun getMessages(): Flow<List<Message>>

    fun getLastMessage(): Flow<Message>

    suspend fun insertMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun deleteMessage(message: Message)

    suspend fun insertAssistant(assistant: Assistant)

    suspend fun insertAllAssistant(assistant: Collection<Assistant>)

    suspend fun updateAssistant(assistant: Assistant)

}