package com.jalloft.michat.ui.screens.chat

import androidx.lifecycle.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.Message
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.repository.MichatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(BetaOpenAI::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val openAI: OpenAI,
    private val repo: MichatRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val currentAssistant: AssistantIdentifier? =
        savedState.get<String>("assistant")?.toAssistant()

    private val _currentMessages = MutableLiveData<List<ChatMessage>>()
    val currentMessages: LiveData<List<ChatMessage>> get() = _currentMessages

    private val _isProcessing = MutableLiveData<Boolean>(false)
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    fun getMessages(assistantIdentifier: AssistantIdentifier) =
        repo.getMessages(assistantIdentifier.id)

    init {
        viewModelScope.launch {

            currentAssistant?.let { assistant ->
                repo.getMessages(assistant.id).collect { messages ->
                    val chatMessages = messages.map { ChatMessage(ChatRole(it.role), it.content) }
                    _currentMessages.postValue(chatMessages)
                }
            }
        }

    }

    private fun addMessage(chatMessage: ChatMessage, onSaved: () -> Unit = {}) {
        currentAssistant?.let {
            val message = Message(
                content = chatMessage.content,
                role = chatMessage.role.role,
                assistantId = it.id,
                timestamp = System.currentTimeMillis()
            )
            viewModelScope.launch(Dispatchers.IO) {
                val saved = repo.insertMessage(message)
                if (saved >= 1) {
                    delay(500)
                    onSaved()
                }
            }
        }

    }


    fun sendMessage(content: String, role: ChatRole) {
        _isProcessing.value = true
        val chatMessage = ChatMessage(role, content)
        addMessage(chatMessage) {
            currentMessages.value?.let {
                chatCompletion(it)
            }
        }
    }


    private fun chatCompletion(messages: List<ChatMessage>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (messages.last().role != ChatRole.Assistant) {
                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = messages
                )
                openAI.chatCompletion(chatCompletionRequest).choices.forEach { choice ->
                    choice.message?.let { chatMessage ->
                        withContext(Dispatchers.Main) {
                            _isProcessing.value = false
                            addMessage(ChatMessage(chatMessage.role, chatMessage.content))
                        }
                    }
                }
            }
        }
    }

}