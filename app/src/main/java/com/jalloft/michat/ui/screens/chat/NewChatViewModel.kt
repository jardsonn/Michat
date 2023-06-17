package com.jalloft.michat.ui.screens.chat

import androidx.lifecycle.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.FirebaseMessage
import com.jalloft.michat.data.getSpecialtyById
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.repository.FirebaseRepository
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.ModelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import timber.log.Timber
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import java.util.UUID
import javax.inject.Inject


@OptIn(BetaOpenAI::class)
@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val openAI: OpenAI,
    private val repo: FirebaseRepository,
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
                Timber.i("CANCELANDO ENVIO DE MENSAGEM")
                job.cancel()
            }
        }
    }


    fun answerLastMessage(chatMessages: List<ChatMessage>? = currentMessages.value) {
        chatMessages?.let { messages ->
            if (messages.isNotEmpty() && messages.first().role != ChatRole.Assistant && isProcessing.value == false) {
                _isProcessing.value = true
                chatCompletion(messages)
            }
        }
//        withContext(Dispatchers.IO){
////            delay(100)
//            chatMessages?.let { messages ->
//                if (messages.isNotEmpty() && messages.first().role != ChatRole.Assistant && isProcessing.value == false) {
//                    _isProcessing.postValue(true)
//                    chatCompletion(messages)
//                }
//            }
//        }
    }

    init {
        viewModelScope.launch {
            currentAssistant?.let { assistant ->
                i("ID DO ASSISTENTE ATUAL: ${assistant.id}")
                repo.getMessages(assistant.id).collect { messages ->
                    val chatMessages = messages
                        .map { ChatMessage(ChatRole(it?.role ?: ""), it?.content ?: "") }
                    _currentMessages.postValue(chatMessages)
                }
            }
        }
    }


    private fun addMessage(chatMessage: ChatMessage, onSaved: () -> Unit = {}) {
        currentAssistant?.let {
            val message = FirebaseMessage(
                messageId = UUID.randomUUID().toString(),
                content = chatMessage.content,
                role = chatMessage.role.role,
                assistantId = it.id,
                timestamp = Timestamp.now(),
//                specialty = getSpecialtyById(it.id).name
            )
            viewModelScope.launch(Dispatchers.IO) {
//                val canSendMessage = repo.verifyCounterMessages().first()
               repo.verifyCounterMessages().collect{ canSendMessage ->
                   if (canSendMessage){
                       val isSaved = repo.sendMessage(message).first()
                       i("MESAGEM SALVA NO FIRESTORE")
                       if (isSaved) {
                           delay(500)
                           onSaved()
                       }
                   }else{
                       e("NÂO TEM PERMISSÃO PARA MANDA MENSAGEM POR QUE ULTRAPASSOU O LIMITE DIARIO")
                   }
               }
            }
        }

    }

    fun sendMessage(
        content: String,
        role: ChatRole,
        isConnected: Boolean = true,
    ) {
        val chatMessage = ChatMessage(role, content)
        addMessage(chatMessage) {
            currentMessages.value?.let {
                i("FOI CHAMADO DO isProcessing = ${isProcessing.value}")
                if (isProcessing.value == false)
                    chatCompletion(it)
            }
            _isProcessing.postValue(isConnected)
        }
    }


    private fun getMessagesPrompt(messages: List<ChatMessage>): List<ChatMessage> {
        val lastTenMessages = messages.filter { message ->
            val uniqueIds = mutableSetOf<String>()
            uniqueIds.add(message.content)
        }.take(QUANTITY_MESSAGE_FOR_PROMPT).toMutableList()
        return lastTenMessages.apply { add(messages.last()) }
    }

    private fun chatCompletion(messages: List<ChatMessage>) {
        sendingMessageJob = viewModelScope.launch(Dispatchers.IO) {
            if (messages.first().role != ChatRole.Assistant) {
                try {
                    val chatCompletionRequest = ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo"),
//                        messages = messages.reversed(),
                        messages = getMessagesPrompt(messages).reversed(),
                        maxTokens = 1000
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
                    Timber.e(error)
                }

            }
        }

    }

    companion object {
        private const val QUANTITY_MESSAGE_FOR_PROMPT = 10
    }

}