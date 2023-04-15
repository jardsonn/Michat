package com.jalloft.michat.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jalloft.michat.data.Assistant
import com.jalloft.michat.data.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(entities = [Assistant::class, Message::class], version = 1)
@TypeConverters(DateTimeTypeConverters::class)
abstract class MichatDatabase : RoomDatabase() {

    abstract fun assistantDao(): AssistantDao
    abstract fun messageDao(): AssistantDao

    companion object {
        private var INSTANCE: MichatDatabase? = null

        fun getDatabase(context: Context): MichatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(context, MichatDatabase::class.java, "michat.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(MichatDatabaseCallback(CoroutineScope(SupervisorJob())))
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

    private class MichatDatabaseCallback(private val coroutineScope: CoroutineScope) : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { coroutineScope.launch { Dispatchers.IO } }
        }
    }
}