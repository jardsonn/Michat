package com.jalloft.michat.data

import androidx.compose.ui.graphics.Color
import com.jalloft.michat.ui.theme.*

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.google.gson.Gson
import com.jalloft.michat.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssistantIdentifier(
    val assistant: AssistantsEnum,
    val specialty: AssistantSpecialty = getAssistantByName(assistant),
    val color: Int = getColorByName(specialty).toArgb()
) : Parcelable


@Parcelize
enum class AssistantSpecialty(@StringRes val stringId: Int) : Parcelable {
    All(R.string.chat),
    Mathematics(R.string.mathematics),
    Music(R.string.music),
    Health(R.string.health),
    Astrology(R.string.astrology),
    Geography(R.string.geography),
    History(R.string.history),
    Science(R.string.science),
    Culinary(R.string.culinary),
    Style(R.string.style),
}

@Parcelize
enum class AssistantsEnum(val stringId: Int = -1): Parcelable{
    FreeChat(R.string.free_talk), Klein, Presley, Galilei, Jenner, Ranke, Curie, Escoffier, Dior, Gauss
}


@Composable
fun AssistantIdentifier.systemMessage() =
    if (assistant == AssistantsEnum.FreeChat) stringResource(R.string.system_opening_message_chat) else stringResource(
        id = R.string.system_opening_message_specialist,
        stringResource(specialty.stringId),
        assistant.name
    )


fun getAssistantByName(name: AssistantsEnum): AssistantSpecialty {
    return when (name) {
        AssistantsEnum.FreeChat -> AssistantSpecialty.All
        AssistantsEnum.Galilei -> AssistantSpecialty.Astrology
        AssistantsEnum.Klein -> AssistantSpecialty.Geography
        AssistantsEnum.Jenner -> AssistantSpecialty.Health
        AssistantsEnum.Presley -> AssistantSpecialty.Music
        AssistantsEnum.Gauss -> AssistantSpecialty.Mathematics
        AssistantsEnum.Ranke -> AssistantSpecialty.History
        AssistantsEnum.Curie -> AssistantSpecialty.Science
        AssistantsEnum.Escoffier -> AssistantSpecialty.Culinary
        AssistantsEnum.Dior -> AssistantSpecialty.Style
    }
}

fun getColorByName(specialty: AssistantSpecialty): Color {
    return when (specialty) {
        AssistantSpecialty.All -> Gray90
        AssistantSpecialty.Mathematics -> VeryLightBlue
        AssistantSpecialty.Astrology -> Purple
        AssistantSpecialty.History -> Orange
        AssistantSpecialty.Science -> DodgerBlue
        AssistantSpecialty.Health -> Green
        AssistantSpecialty.Geography -> Yellow
        AssistantSpecialty.Music -> Red
        AssistantSpecialty.Culinary -> Limerick
        AssistantSpecialty.Style -> TiffanyBlue
    }
}

fun AssistantIdentifier.toJson(): String = Gson().toJson(this)

fun String.toAssistant(): AssistantIdentifier = Gson().fromJson(this, AssistantIdentifier::class.java)
