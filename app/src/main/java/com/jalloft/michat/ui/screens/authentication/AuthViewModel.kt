package com.jalloft.michat.ui.screens.authentication

import androidx.lifecycle.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.FirebaseMessage
import com.jalloft.michat.data.UserData
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.repository.FirebaseRepository
import com.jalloft.michat.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.*
import javax.inject.Inject

@OptIn(BetaOpenAI::class)
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: FirebaseRepository,
    savedState: SavedStateHandle,
) : ViewModel() {

    val currentAssistant: AssistantIdentifier? =
        savedState.get<String>("assistant")?.toAssistant()


    init {
        viewModelScope.launch {
//            repo.getMessages().collect{
//                i("Minhas mensagens: ${it}")
//            }
//            repo.sendMessage(
//                FirebaseMessage(
//                messageId = UUID.randomUUID().toString(),
//                content = "meu texto aqui responde ai 2 ",
//                timestamp = System.currentTimeMillis(),
//                role = ChatRole.User.role,
//                assistantId = 1
//            )
//            )
        }
    }


    fun signOut() = repo.signOut()

    fun isAuthenticated(): Boolean = repo.isAuthenticated()

    private val _reauthenticateState = MutableLiveData<Response<Unit>>()
    val reauthenticateState: LiveData<Response<Unit>> get() = _reauthenticateState

    private val _deleteUserState = MutableLiveData<Response<Unit>>()
    val deleteUserState: LiveData<Response<Unit>> get() = _deleteUserState

    private val _updatePasswordState = MutableLiveData<Response<Unit>>()
    val updatePasswordState: LiveData<Response<Unit>> get() = _updatePasswordState

    private val _signInWithGoogleState = MutableLiveData<Response<Boolean>>()
    val signInWithGoogleState: LiveData<Response<Boolean>> get() = _signInWithGoogleState

    private val _createUserWithEmailAndPasswordState = MutableLiveData<Response<UserData>>()
    val createUserWithEmailAndPasswordState: LiveData<Response<UserData>> get() = _createUserWithEmailAndPasswordState

    private val _signInWithEmailAndPasswordState = MutableLiveData<Response<UserData>>()
    val signInWithEmailAndPasswordState: LiveData<Response<UserData>> get() = _signInWithEmailAndPasswordState

    private val _editUserDataState = MutableLiveData<UpdateUserDataState>()
    val editUserDataState: LiveData<UpdateUserDataState> get() = _editUserDataState

    fun getUser() = repo.getUser()

    fun editUserData(name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.editUser(name, email).collect {
                when (val reponse = it) {
                    is Response.Loading -> {
                        _editUserDataState.postValue(UpdateUserDataState.Loading)
                    }
                    is Response.Success -> {
                        _editUserDataState.postValue(UpdateUserDataState.Sucess)
                    }
                    is Response.Failure -> {
                        _editUserDataState.postValue(UpdateUserDataState.Failure(reponse.exception))
                    }
                }
            }
        }
    }

    fun reauthenticate(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.reauthenticate(email, password).collect {
                _reauthenticateState.postValue(it)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.reauthenticate(credential).collect {
                _reauthenticateState.postValue(it)
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteUser().collect {
                _deleteUserState.postValue(it)
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updatePassword(newPassword).collect {
                _updatePasswordState.postValue(it)
            }
        }
    }

    fun signInWithGoogle(dToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.signInWithGoogle(dToken).collect {
                _signInWithGoogleState.postValue(it)
            }
        }
    }

    fun createUserWithEmailAndPassword(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createUserWithEmailAndPassword(name, email, password).collect {
                _createUserWithEmailAndPasswordState.postValue(it)
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.signInWithEmailAndPassword(email, password).collect {
                _signInWithEmailAndPasswordState.postValue(it)
            }
        }
    }

    sealed class UpdateUserDataState {
        object Loading : UpdateUserDataState()
        data class Failure(val exception: Exception) : UpdateUserDataState()
        object Sucess : UpdateUserDataState()
    }

}