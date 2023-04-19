package com.jalloft.michat.data

import androidx.compose.ui.graphics.Color
import com.jalloft.michat.ui.theme.*

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.google.gson.Gson
import com.jalloft.michat.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssistantIdentifier(
    val assistant: AssistantsEnum,
    val specialtyNameId: Int = getAssistantByName(assistant),
    val color: Int = getColorByName(assistant).toArgb(),
    val id: Int = getIdByName(assistant)
) : Parcelable

fun getIdByName(assistant: AssistantsEnum): Int {
    return when (assistant) {
        AssistantsEnum.FreeChat -> 0
        AssistantsEnum.Galilei -> 1
        AssistantsEnum.Klein -> 2
        AssistantsEnum.Jenner -> 3
        AssistantsEnum.Presley -> 4
        AssistantsEnum.Gauss -> 5
        AssistantsEnum.Ranke -> 6
        AssistantsEnum.Curie -> 7
        AssistantsEnum.Escoffier -> 8
        AssistantsEnum.Dior -> 9
    }
}


//@Parcelize
//enum class AssistantSpecialty(@StringRes val stringId: Int) : Parcelable {
//    All(R.string.chat),
//    Mathematics(R.string.mathematics),
//    Music(R.string.music),
//    Health(R.string.health),
//    Astrology(R.string.astrology),
//    Geography(R.string.geography),
//    History(R.string.history),
//    Science(R.string.science),
//    Culinary(R.string.culinary),
//    Style(R.string.style),
//}

@Parcelize
enum class AssistantsEnum(val stringId: Int = -1) : Parcelable {
    FreeChat(R.string.free_talk), Klein, Presley, Galilei, Jenner, Ranke, Curie, Escoffier, Dior, Gauss
}


@Composable
fun AssistantIdentifier.systemMessage() =
    if (assistant == AssistantsEnum.FreeChat) stringResource(R.string.system_opening_message_chat) else stringResource(
        id = R.string.system_opening_message_specialist,
        stringResource(specialtyNameId),
        assistant.name
    )


fun getAssistantByName(name: AssistantsEnum): Int {
    return when (name) {
        AssistantsEnum.FreeChat -> R.string.chat
        AssistantsEnum.Gauss -> R.string.mathematics
        AssistantsEnum.Galilei -> R.string.astrology
        AssistantsEnum.Ranke -> R.string.history
        AssistantsEnum.Curie -> R.string.science
        AssistantsEnum.Jenner -> R.string.health
        AssistantsEnum.Klein -> R.string.geography
        AssistantsEnum.Presley -> R.string.music
        AssistantsEnum.Escoffier -> R.string.culinary
        AssistantsEnum.Dior -> R.string.style
    }
}

fun getColorByName(assistant: AssistantsEnum): Color {
    return when (assistant) {
        AssistantsEnum.FreeChat -> SmokyBlack
        AssistantsEnum.Gauss -> VeryLightBlue
        AssistantsEnum.Galilei -> Purple
        AssistantsEnum.Ranke -> Orange
        AssistantsEnum.Curie -> DodgerBlue
        AssistantsEnum.Jenner -> Green
        AssistantsEnum.Klein -> Yellow
        AssistantsEnum.Presley -> Red
        AssistantsEnum.Escoffier -> Limerick
        AssistantsEnum.Dior -> TiffanyBlue
    }
}

fun AssistantIdentifier.toJson(): String = Gson().toJson(this)

fun String.toAssistant(): AssistantIdentifier =
    Gson().fromJson(this, AssistantIdentifier::class.java)
