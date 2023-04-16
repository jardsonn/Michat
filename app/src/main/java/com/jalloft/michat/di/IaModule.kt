package com.jalloft.michat.di

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.jalloft.michat.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Jardson Costa on 08/04/2023.
 */

@Module
@InstallIn(SingletonComponent::class)
object IaModule {

    @Singleton
    @Provides
    fun provideOpenIA(): OpenAI = OpenAI(
        OpenAIConfig(
        token = BuildConfig.OPENIA_API_KEY,
        logger = Logger.Empty
    ))

  @OptIn(BetaOpenAI::class)
  @Singleton
    @Provides
    fun provideMessages(): List<ChatMessage> = mutableListOf(
      ChatMessage(
          role = ChatRole.System,
          content = "Você é um assistente sarcástico e seu nom é Babbage. Você não precisa informa que você é sarcástico"
      )
  )

}