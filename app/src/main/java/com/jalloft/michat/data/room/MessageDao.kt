package com.jalloft.michat.data.room

import androidx.room.*
import com.jalloft.michat.data.Message
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM messages WHERE assistant_id = :assistantId")
    abstract fun getMessagesByAssistantId(assistantId: Int): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE assistant_id = :assistantId ORDER BY timestamp DESC LIMIT 1")
    abstract fun getLatestMessageByAssistantId(assistantId: Int): Flow<Message>?

    @Query("SELECT * FROM messages WHERE role != 'system' AND id IN (SELECT MAX(id) FROM messages WHERE role != 'system' GROUP BY assistant_id) ORDER BY timestamp DESC")
    abstract fun getLastMessages(): Flow<List<Message>>?


}