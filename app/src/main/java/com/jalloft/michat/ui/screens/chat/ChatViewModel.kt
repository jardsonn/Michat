package com.jalloft.michat.ui.screens.chat

import androidx.lifecycle.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(BetaOpenAI::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val openAI: OpenAI,
    initialMessages: List<ChatMessage>
) : ViewModel() {

    private val _messages: MutableLiveData<List<ChatMessage>> = MutableLiveData(initialMessages)
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val _processing: MutableLiveData<Boolean> = MutableLiveData(false)
    val processing: LiveData<Boolean> get() = _processing

    private fun setProcessing(isProcessing: Boolean) {
        _processing.postValue(isProcessing)
    }

    private fun addMessage(chatMessage: ChatMessage) {
        _messages.value = mutableListOf<ChatMessage>().apply {
            messages.value?.let { addAll(it) }
            add(chatMessage)
        }
//        _messages.postValue(mutableListOf<ChatMessage>().apply {
//            messages.value?.let { addAll(it) }
//            add(chatMessage)
//        })
    }

    fun sendMessage(message: String) {
        setProcessing(true)
        addMessage(ChatMessage(ChatRole.User, message))
        messages.value?.let { chatCompletion(it) }
    }

    private fun chatCompletion(messages: List<ChatMessage>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (messages.last().role == ChatRole.User) {
                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = messages
                )
                openAI.chatCompletion(chatCompletionRequest).choices.forEach { choice ->
                    choice.message?.let { chatMessage ->
                        setProcessing(false)
                        withContext(Dispatchers.Main){
                            addMessage(ChatMessage(chatMessage.role, chatMessage.content))
                        }
                    }
                }
            }
        }
    }

}