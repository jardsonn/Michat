package com.jalloft.michat.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.jalloft.michat.data.Message
import com.jalloft.michat.repository.MichatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MichatRepository,
) : ViewModel() {

    val lastMessages: MutableStateFlow<LatestMessagesState> = MutableStateFlow(LatestMessagesState.Empty)

    init {
        viewModelScope.launch {
            repo.getLastMessages()?.collect {
                lastMessages.value = LatestMessagesState.LastMessages(it)
//                lastMessages.value = lastMessages.value.copy()
            }
        }
    }

    sealed class LatestMessagesState {
        object Empty : LatestMessagesState()
        data class LastMessages(val messages: List<Message>) : LatestMessagesState()
    }
}