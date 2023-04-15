package com.jalloft.michat.data.room

import androidx.room.*
import com.jalloft.michat.data.Assistant
import com.jalloft.michat.data.AssistantWithMessage
import com.jalloft.michat.data.Message
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AssistantDao : BaseDao<Assistant> {

    @Transaction
    @Query("SELECT * FROM assistant")
    abstract fun getMessageWithAssistant(): Flow<List<AssistantWithMessage>>

    @Query("SELECT * FROM assistant")
    abstract fun getAssistants(): Flow<List<Assistant>>

}