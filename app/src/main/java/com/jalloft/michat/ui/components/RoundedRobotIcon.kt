package com.jalloft.michat.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jalloft.michat.R
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.AssistantsEnum
import com.jalloft.michat.ui.theme.White


@Composable
fun RoundedRobotIcon(assistant: AssistantIdentifier, size: Dp, shape: Shape) {
    Surface(
        modifier = Modifier
            .size(size),
        shape = shape,
        color = Color(assistant.color),
        contentColor = if (assistant.assistant == AssistantsEnum.FreeChat) White.copy(.6f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Icon(
            painter = painterResource(id = if (assistant.assistant == AssistantsEnum.FreeChat) R.drawable.ic_free_chat else R.drawable.robot_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
        )
    }
}