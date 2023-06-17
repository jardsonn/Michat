package com.jalloft.michat.data

import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jardson Costa on 23/04/2023.
 */
data class FirebaseMessage(
    var messageId: String = "",
    var content: String = "",
    val timestamp: Timestamp? = null,
    val role: String = "",
    val assistantId: Int = -1,
) {

//    @Exclude
//    private fun Date.formatted(): String? = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(this)
//
//    @Exclude
//    override fun toString(): String {
//        return "${timestamp?.toDate()?.formatted()} - ${getAssistant().specialty.name} ($role)"
//    }
//
    @Exclude
    fun getAssistant(): AssistantIdentifier {
        return AssistantIdentifier(assistant = getAssistantById(assistantId))
    }
//
    @Exclude
    private fun getAssistantById(assistantId: Int): AssistantsEnum {
        return when (assistantId) {
            0 -> AssistantsEnum.FreeChat
            1 -> AssistantsEnum.Galilei
            2 -> AssistantsEnum.Klein
            3 -> AssistantsEnum.Jenner
            4 -> AssistantsEnum.Presley
            5 -> AssistantsEnum.Gauss
            6 -> AssistantsEnum.Ranke
            7 -> AssistantsEnum.Curie
            8 -> AssistantsEnum.Escoffier
            9 -> AssistantsEnum.Dior
            else -> {
                AssistantsEnum.FreeChat
            }
        }
    }
}
