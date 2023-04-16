package com.jalloft.michat.repository

import com.jalloft.michat.data.Message
import com.jalloft.michat.data.room.MessageDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MichatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao
) : MichatRepository {
    override fun getMessages(assistantId: Int): Flow<List<Message>> {
        return messageDao.getMessagesByAssistantId(assistantId)
    }

    override fun getLatestMessageByAssistantId(assistantId: Int): Flow<Message>? {
        return messageDao.getLatestMessageByAssistantId(assistantId)
    }

    override fun getLastMessages(): Flow<List<Message>>? {
        return messageDao.getLastMessages()
    }

    override suspend fun insertMessage(message: Message): Long {
      return messageDao.insert(message)
    }

    override suspend fun insertAllMessage(messages: Collection<Message>) {
        messageDao.insertAll(messages)
    }

    override suspend fun updateMessage(message: Message) {
        messageDao.update(message)
    }

    override suspend fun deleteMessage(message: Message) {
        messageDao.delete(message)
    }


}