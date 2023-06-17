package com.jalloft.michat.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalloft.michat.data.FirebaseMessage
import com.jalloft.michat.data.Message
import com.jalloft.michat.repository.FirebaseRepository
import com.jalloft.michat.repository.MichatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MichatRepository,
    private val authRepo: FirebaseRepository,
) : ViewModel() {

    private val _lastMessages: MutableStateFlow<LatestMessagesState> =
        MutableStateFlow(LatestMessagesState.Empty)

    val lastMessages: StateFlow<LatestMessagesState> = _lastMessages

    fun isAuthenticated() = authRepo.isAuthenticated()

    init {
        viewModelScope.launch {
            loadLatestMessages()
        }
    }

    private suspend fun loadLatestMessages() {
        _lastMessages.value = LatestMessagesState.Loading
        authRepo.getLatestMessages().flowOn(Dispatchers.IO).collect { messages ->
            _lastMessages.value = LatestMessagesState.LastMessages(messages)
        }
        delay(500)
        if (lastMessages.value is LatestMessagesState.Loading) {
            _lastMessages.value = LatestMessagesState.Empty
        }
    }

    sealed class LatestMessagesState {
        object Empty : LatestMessagesState()
        object Loading : LatestMessagesState()
        data class LastMessages(val messages: List<FirebaseMessage>) : LatestMessagesState()
    }
}