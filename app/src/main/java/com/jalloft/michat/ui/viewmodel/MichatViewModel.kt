package com.jalloft.michat.ui.viewmodel

import com.aallam.openai.api.BetaOpenAI
import com.jalloft.michat.repository.MichatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(BetaOpenAI::class)
@HiltViewModel
class MichatViewModel @Inject constructor(private val repo: MichatRepository) {


}