package com.jalloft.michat.di

import android.content.Context
import com.jalloft.michat.data.room.MessageDao
import com.jalloft.michat.data.room.MichatDatabase
import com.jalloft.michat.repository.MichatRepository
import com.jalloft.michat.repository.MichatRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Jardson Costa on 15/04/2023.
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MichatDatabase {
        return MichatDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideMessageDao(database: MichatDatabase): MessageDao {
        return database.messageDao()
    }

    @Singleton
    @Provides
    fun provideRepository(messageDao: MessageDao): MichatRepository =
        MichatRepositoryImpl(messageDao)

}