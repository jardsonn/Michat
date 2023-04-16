package com.jalloft.michat.repository

import com.jalloft.michat.data.Message
import kotlinx.coroutines.flow.Flow


interface MichatRepository {

    fun getMessages(assistantId: Int): Flow<List<Message>>

    fun getLatestMessageByAssistantId(assistantId: Int): Flow<Message>?

    fun getLastMessages(): Flow<List<Message>>?

    suspend fun insertMessage(message: Message): Long

    suspend fun insertAllMessage(messages: Collection<Message>)

    suspend fun updateMessage(message: Message)

    suspend fun deleteMessage(message: Message)

}