package com.jalloft.michat.repository

import com.jalloft.michat.data.Assistant
import com.jalloft.michat.data.AssistantWithMessage
import com.jalloft.michat.data.Message
import com.jalloft.michat.data.room.AssistantDao
import com.jalloft.michat.data.room.MessageDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MichatRepositoryImpl @Inject constructor(
    private val assistantDao: AssistantDao,
    private val messageDao: MessageDao
) : MichatRepository {

    override fun getMessageWithAssistant(): Flow<List<AssistantWithMessage>> {
        return assistantDao.getMessageWithAssistant()
    }

    override fun getAssistants(): Flow<List<Assistant>> {
        return assistantDao.getAssistants()
    }

    override fun getMessagesByAssistant(idAssistant: Int): Flow<List<Message>> {
        return messageDao.getMessagesByAssistant(idAssistant)
    }

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getMessages()
    }

    override fun getLastMessage(): Flow<Message> {
        return messageDao.getLastMessage()
    }

    override suspend fun insertMessage(message: Message) {
        messageDao.insert(message)
    }

    override suspend fun updateMessage(message: Message) {
        messageDao.update(message)
    }

    override suspend fun deleteMessage(message: Message) {
        messageDao.delete(message)
    }

    override suspend fun insertAssistant(assistant: Assistant) {
        assistantDao.insert(assistant)
    }

    override suspend fun insertAllAssistant(assistant: Collection<Assistant>) {
        assistantDao.insertAll(assistant)
    }

    override suspend fun updateAssistant(assistant: Assistant) {
        assistantDao.update(assistant)
    }

}