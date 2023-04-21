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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.last
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
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

    private var sendingMessageJob: Job? = null

    fun cancelSendingMessage() {
        sendingMessageJob?.let { job ->
            if (job.isActive) {
                i("CANCELANDO ENVIO DE MENSAGEM")
                job.cancel()
            }
        }
    }

    fun answerLastMessage(chatMessages: List<ChatMessage>? = currentMessages.value) {
//        if ((sendingMessageJob == null || sendingMessageJob?.isActive == false) && isProcessing.value == false) {
            chatMessages?.let { messages ->
                if (messages.isNotEmpty() && messages.last().role != ChatRole.Assistant) {
                    _isProcessing.value = true
                    chatCompletion(messages)
                }
            }
//        }
    }

    fun getMessages(assistantIdentifier: AssistantIdentifier) =
        repo.getMessages(assistantIdentifier.id)

    init {
        viewModelScope.launch {
            currentAssistant?.let { assistant ->
                repo.getMessages(assistant.id).collect { messages ->
                    val chatMessages = messages.map { ChatMessage(ChatRole(it.role), it.content) }
                    _currentMessages.postValue(chatMessages/*.reversed()*/)
                }
            }
            delay(100)
            answerLastMessage()
            i("Esta processando 2: $isProcessing")
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


    fun sendMessage(content: String, role: ChatRole, isConnected: Boolean = true) {
        _isProcessing.value = isConnected
        val chatMessage = ChatMessage(role, content)
        addMessage(chatMessage) {
            currentMessages.value?.let {
                chatCompletion(it)
            }
        }
    }


    private fun chatCompletion(messages: List<ChatMessage>) {
        sendingMessageJob = viewModelScope.launch(Dispatchers.IO) {
            if (messages.first().role != ChatRole.Assistant) {
                try {
                    val chatCompletionRequest = ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo"),
                        messages = messages.reversed()
                    )
                    openAI.chatCompletion(chatCompletionRequest).choices.forEach { choice ->
                        choice.message?.let { chatMessage ->
                            withContext(Dispatchers.Main) {
                                addMessage(ChatMessage(chatMessage.role, chatMessage.content))
                                _isProcessing.value = false
                            }
                        }
                    }
                } catch (error: Throwable) {
                    _isProcessing.postValue(false)
                    e(error)
                }

            }
        }

    }

}