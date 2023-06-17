package com.jalloft.michat.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.jalloft.michat.data.FirebaseMessage
import com.jalloft.michat.data.UserData
import com.jalloft.michat.utils.Response
import kotlinx.coroutines.flow.Flow


interface FirebaseRepository {

    fun signOut()

    fun isAuthenticated(): Boolean

    suspend fun reauthenticate(email: String, password: String): Flow<Response<Unit>>

    suspend fun editUser(name: String, email: String): Flow<Response<Unit>>

    suspend fun reauthenticate(credential: AuthCredential): Flow<Response<Unit>>

    suspend fun deleteUser(): Flow<Response<Unit>>

    suspend fun updatePassword(newPassword: String): Flow<Response<Unit>>

    suspend fun signInWithGoogle(idToken: String): Flow<Response<Boolean>>

    suspend fun createUserInFirestore(createdAt: FieldValue? = null): Flow<Response<Void>>

    fun getUser(): FirebaseUser?

    suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Flow<Response<UserData>>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Response<UserData>>

    suspend fun getMessages(assistantId: Int): Flow<List<FirebaseMessage?>>

    suspend fun sendMessage(message: FirebaseMessage): Flow<Boolean>

    suspend fun getLatestMessages(): Flow<List<FirebaseMessage>>

    fun resetCounterMessages()

    suspend fun verifyCounterMessages(): Flow<Boolean>

}