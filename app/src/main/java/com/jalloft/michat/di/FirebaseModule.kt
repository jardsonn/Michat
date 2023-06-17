package com.jalloft.michat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jalloft.michat.repository.FirebaseRepository
import com.jalloft.michat.repository.FirebaseRepositoryImpl
import com.jalloft.michat.utils.MESSAGES_REF
import com.jalloft.michat.utils.MESSAGE_COUNTER_REF
import com.jalloft.michat.utils.USERS_REF
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

/**
 * Created by Jardson Costa on 21/04/2023.
 */


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Named(USERS_REF)
    fun provideUsersRef(db: FirebaseFirestore) = db.collection(USERS_REF)

     @Provides
    @Named(MESSAGE_COUNTER_REF)
    fun provideMessagesCounterRef(db: FirebaseFirestore) = db.collection(MESSAGE_COUNTER_REF)



    @Provides
    @Named(MESSAGES_REF)
    fun provideMessagesRef(db: FirebaseFirestore) = db.collection(MESSAGES_REF)


    @Provides
    fun provideAuthRepositoy(
        auth: FirebaseAuth,
        @Named(USERS_REF) usersRef: CollectionReference,
        @Named(MESSAGE_COUNTER_REF) messagesCounterRef: CollectionReference,
    ): FirebaseRepository = FirebaseRepositoryImpl(auth, usersRef, messagesCounterRef)

//    @Provides
//    fun provideResetCounterMessagesReceiver(repository: FirebaseRepository): ResetCounterMessagesReceiver {
//        return ResetCounterMessagesReceiver(repository)
//    }
}