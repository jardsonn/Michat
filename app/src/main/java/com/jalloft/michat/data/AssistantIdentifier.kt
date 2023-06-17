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
    val specialtyNameStringId: Int = getAssistantByName(assistant),
    val color: Int = getColorByName(assistant).toArgb(),
    val id: Int = getIdByName(assistant),
    val specialty: Specialty = getSpecialtyById(id)
) : Parcelable

fun getSpecialtyById(id: Int): Specialty {
    return when(id){
        0 -> Specialty.All
        1 -> Specialty.Astrology
        2 -> Specialty.Geography
        3 -> Specialty.Health
        4 -> Specialty.Music
        5 -> Specialty.Mathematics
        6 -> Specialty.History
        7 -> Specialty.Science
        8 -> Specialty.Culinary
        9 -> Specialty.Style
        else -> Specialty.All
    }
}


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

@Parcelize
enum class Specialty : Parcelable {
    All,
    Mathematics,
    Music,
    Health,
    Astrology,
    Geography,
    History,
    Science,
    Culinary,
    Style,
}

@Parcelize
enum class AssistantsEnum(val stringId: Int = -1) : Parcelable {
    FreeChat(R.string.free_talk), Klein, Presley, Galilei, Jenner, Ranke, Curie, Escoffier, Dior, Gauss
}


@Composable
fun AssistantIdentifier.systemMessage() =
    if (assistant == AssistantsEnum.FreeChat) stringResource(R.string.system_opening_message_chat) else stringResource(
        id = R.string.system_opening_message_specialist,
        stringResource(specialtyNameStringId),
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

fun String.toAssistant(): AssistantIdentifier? = try {
    Gson().fromJson(this, AssistantIdentifier::class.java)
}catch (e: Exception){
    null
}

