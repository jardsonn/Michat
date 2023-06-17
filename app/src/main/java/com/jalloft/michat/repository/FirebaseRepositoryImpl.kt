package com.jalloft.michat.repository

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Query
import com.jalloft.michat.data.FirebaseMessage
import com.jalloft.michat.data.MessageCounter
import com.jalloft.michat.data.UserData
import com.jalloft.michat.utils.*
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.ModelType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber.Forest.d
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import timber.log.Timber.Forest.w
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named


class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @Named(USERS_REF) private val usersRef: CollectionReference,
    @Named(MESSAGE_COUNTER_REF) private val messagesCounterRef: CollectionReference,
) : FirebaseRepository {
    override suspend fun signInWithEmailAndPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            d("signInWithEmail:success")
            emit(Response.Success(UserData(name = user?.displayName, email = user?.email)))
        } catch (e: Exception) {
            w("signInWithEmail:failure", e.message)
            emit(log(e))
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ) = flow {
        try {
            emit(Response.Loading)
            auth.createUserWithEmailAndPassword(email, password).await()?.let {
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user?.updateProfile(profileUpdates)?.await()
                user?.sendEmailVerification()
                createUserInFirestore(serverTimestamp()).collect {
                    if (it !is Response.Loading) {
                        emit(
                            Response.Success(
                                UserData(
                                    name = user?.displayName,
                                    email = user?.email
                                )
                            )
                        )
                    }
                }
            }
            d("createUserWithEmail:success")
        } catch (e: Exception) {
            w("createUserWithEmail:failure", e.message)
            emit(log(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String) = flow {
        try {
            emit(Response.Loading)
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            authResult.additionalUserInfo?.apply {
                if (isNewUser) {
                    createUserInFirestore(serverTimestamp()).collect {
                        if (it !is Response.Loading) {
                            emit(Response.Success(isNewUser))
                        }
                    }
                } else {
                    emit(Response.Success(isNewUser))
                }

            }
        } catch (e: Exception) {
            w("signInWithGoogle:failure", e.message)
            emit(log(e))
        }
    }

    override fun getUser() = auth.currentUser

    override suspend fun createUserInFirestore(createdAt: FieldValue?) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                usersRef.document(uid).set(
                    UserData(
                        name = displayName,
                        email = email,
                        createdAt = createdAt,
                        updatedAt = serverTimestamp()
                    )
                ).await().also {
                    i("DADOS DO USUARIO SALVO COM SUCESSO")
                    emit(Response.Success(it))
                }
            }
        } catch (e: Exception) {
            w("createUserInFirestore:failure", e.message)
            emit(log(e))
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isAuthenticated() = auth.currentUser != null

    override suspend fun reauthenticate(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser?.reauthenticate(credential)?.await()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            w("reauthenticate:failure", e.message)
            emit(log(e))
        }
    }

    override suspend fun reauthenticate(credential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.reauthenticate(credential)?.await()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            w("reauthenticate:failure", e.message)
            emit(log(e))
        }
    }

    override suspend fun deleteUser() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.delete()?.await()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            w("deleteUser:failure", e.message)
            emit(log(e))
        }
    }

    override suspend fun updatePassword(newPassword: String) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.updatePassword(newPassword)?.await()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            w("updatePassword:failure", e.message)
            emit(log(e))
        }
    }

    override fun resetCounterMessages() {
        auth.currentUser?.run {
            try {
                val counterRef = messagesCounterRef.document(uid)
                val now = System.currentTimeMillis()
                counterRef.update(
                    mapOf(
                        "counter" to 0,
                        "lastUpdate" to now
                    )
                )
                i("Contagem resetada com sucesso")
            }catch (e: Throwable){
                e("Ocorreu um erro ao resetar a contagem ${e.message}")
            }
        }
    }

    override suspend fun verifyCounterMessages() = flow {
        auth.currentUser?.run {
            val counterRef = messagesCounterRef.document(uid)
            val now = System.currentTimeMillis()

            try {
                val counterSnapshot = counterRef.get().await()
                val counter = counterSnapshot.toObject(MessageCounter::class.java)
                i("CONTAGEM ATUAL = ${counter?.counter}")
                i("TEMPO DE ULTIMO UPDATE = ${counter?.lastUpdate}")
                if (counter != null){
                    if (counter.counter < 3 && now - (counter.lastUpdate) >= 24 * 60 * 60 * 1000){
                        counterRef.update(
                            mapOf(
                                "counter" to (counter.counter.plus(1)),
                                "lastUpdate" to now
                            )
                        )
                        emit(true)
                    }else{
                        emit(false)
                    }
                }else{
                    counterRef.set(MessageCounter(
                        counter = 1,
                        lastUpdate = now
                    ))
                    emit(true)
                }
//                if ((counter?.counter ?: 0) < 10 && now - (counter?.lastUpdate
//                        ?: 0) >= 24 * 60 * 60 * 1000
//                ) {
//                    counterRef.update(
//                        mapOf(
//                            "counter" to (counter?.counter?.plus(1) ?: 1),
//                            "lastUpdate" to now
//                        )
//                    )
//                    emit(true)
//                } else {
//                    emit(false)
//                }
            } catch (e: Exception) {
                emit(false)
                e.printStackTrace()
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun sendMessage(message: FirebaseMessage) = callbackFlow {
        auth.currentUser?.run {
            try {
                val messageRef = usersRef.document(uid).collection("messages")

                messageRef.document(message.messageId).set(message).await()
                trySend(true)
//                if (message.role == ChatRole.Assistant.role && message.content.length > 100) {
//                    val tokens = encoding.countTokens(message.content)
//                    if (tokens >= MAXIMUM_AMOUNT_OF_TOKENS && message.role == ChatRole.Assistant.role) {
//                        val splitMessage = message.content.splitMessage()
//                        var messageId = message.messageId
//                        splitMessage.forEach {
//                            messageRef.document(messageId).set(message.apply {
//                                this.messageId = messageId
//                                content = it
//                            }).await()
//                            messageId = UUID.randomUUID().toString()
//                        }
//                        i("Mensagem foi dividida em ${splitMessage.size}, tokens = $tokens")
//                    } else {
//                        i("Mensagem salva normalmente, tokens = $tokens")
//                        messageRef.document(message.messageId).set(message).await()
//                    }
//                } else {
//                    i("Mensagem salva normalmente")
//                    messageRef.document(message.messageId).set(message).await()
//                }
//                trySend(true)
            } catch (e: Throwable) {
                trySend(false)
                e.printStackTrace()
            }
        }

        awaitClose { close() }
    }

    override suspend fun getMessages(assistantId: Int): Flow<List<FirebaseMessage?>> =
        callbackFlow {
            val subscription = auth.currentUser?.run {
                val messagesCollection = usersRef.document(uid).collection("messages")
                val query = messagesCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                query.addSnapshotListener { value, _ ->
                    if (value == null) return@addSnapshotListener
                    try {
                        val messageList = value.map { it.toObject(FirebaseMessage::class.java) }
                            .filter { it.assistantId == assistantId }.toMutableList()
                        trySend(messageList)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
            awaitClose { subscription?.remove() }
        }

    override suspend fun getLatestMessages() = callbackFlow {
        val subscription = auth.currentUser?.run {
            val messagesCollection = usersRef.document(uid).collection("messages")
            val query = messagesCollection
                .whereNotEqualTo("role", "system")
                .orderBy("role", Query.Direction.ASCENDING)
//                .orderBy("timestamp", Query.Direction.DESCENDING)

            query.addSnapshotListener { value, _ ->
                if (value == null) return@addSnapshotListener
                try {
                    val lastMessages = value.documents.groupBy { it.get("assistantId") }
                        .mapNotNull { (_, docs) ->
                            docs.maxByOrNull { it.getTimestamp("timestamp")!! }
                                ?.toObject(FirebaseMessage::class.java)
                        }.sortedByDescending { it.timestamp }

                    trySend(lastMessages)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        awaitClose { subscription?.remove() }
    }


    override suspend fun editUser(name: String, email: String): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            val user = auth.currentUser
            user?.updateEmail(email)?.await()
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user?.updateProfile(profileUpdates)?.await()
            createUserInFirestore(null).collect {
                if (it !is Response.Loading) {
                    emit(Response.Success(Unit))
                }
            }
        } catch (e: Exception) {
            emit(log(e))
        }

    }

    private fun log(e: Exception): Response.Failure {
        return Response.Failure(e, e.message ?: ERROR_MESSAGE)
    }


    companion object {
        const val MAXIMUM_AMOUNT_OF_TOKENS = 450
    }

}