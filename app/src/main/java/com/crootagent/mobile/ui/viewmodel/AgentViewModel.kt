package com.crootagent.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crootagent.mobile.data.model.ChatMessage
import com.crootagent.mobile.data.repository.AgentRepository
import kotlinx.coroutines.launch

class AgentViewModel(
    private val repository: AgentRepository = AgentRepository()
) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _activeModel = MutableLiveData(repository.activeModel)
    val activeModel: LiveData<String> = _activeModel

    private val _provider = MutableLiveData(repository.provider)
    val provider: LiveData<String> = _provider

    init {
        // Initial welcome message
        val welcome = ChatMessage(
            role = "assistant",
            content = "Selamat datang di CRootAgent Mobile 🦀\n" +
                    "Remote agent console untuk autonomous software engineering. Gunakan tombol perintah cepat di atas atau ketik prompt untuk memulai.",
            model = repository.activeModel
        )
        _messages.value = listOf(welcome)
    }

    fun updateConfig(newProvider: String, newModel: String, newApiKey: String, newBaseUrl: String) {
        repository.provider = newProvider
        repository.activeModel = newModel
        repository.apiKey = newApiKey
        repository.baseUrl = newBaseUrl
        _activeModel.value = newModel
        _provider.value = newProvider
    }

    fun sendMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return

        val userMessage = ChatMessage(
            role = "user",
            content = trimmed
        )

        val currentList = _messages.value.orEmpty().toMutableList()
        currentList.add(userMessage)
        _messages.value = currentList

        _isLoading.value = true

        viewModelScope.launch {
            val response = repository.execute(trimmed, currentList)
            val updatedList = _messages.value.orEmpty().toMutableList()
            updatedList.add(response)
            _messages.value = updatedList
            _isLoading.value = false
        }
    }

    fun executeQuickCommand(command: String) {
        sendMessage(command)
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}
