package com.jalloft.michat.data

import com.google.firebase.firestore.FieldValue
import java.util.*

/**
 * Created by Jardson Costa on 21/04/2023.
 */
data class UserData(
    var name: String? = null,
    var email: String? = null,
    var createdAt: FieldValue? = null,
    var updatedAt: FieldValue? = null,
)
