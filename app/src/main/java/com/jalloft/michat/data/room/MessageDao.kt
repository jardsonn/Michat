package com.jalloft.michat.data.room

import androidx.room.*
import com.jalloft.michat.data.Message
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao : BaseDao<Message> {

    @Transaction
    @Query("SELECT * FROM messages WHERE id_assistant = :idAssistant")
    abstract fun getMessagesByAssistant(idAssistant: Int): Flow<List<Message>>

    @Transaction
    @Query("SELECT * FROM messages")
    abstract fun getMessages(): Flow<List<Message>>

    @Transaction
    @Query("SELECT * FROM messages ORDER BY date DESC LIMIT 1")
    abstract fun getLastMessage(): Flow<Message>

//    @Insert
//    abstract fun insertMessage(message: Message)
//
//    @Update
//    abstract fun updateMessage(message: Message)
//
//    @Delete
//    abstract fun deleteMessage(message: Message)

}