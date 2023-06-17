package com.jalloft.michat.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jalloft.michat.R
import timber.log.Timber.Forest.i

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("""[a-zA-Z\d._%+-]+@[a-zA-Z\d.-]+\.[a-zA-Z]{2,}""")
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

fun isTextValid(text: String): Boolean = text.length >= 3

@Immutable
data class TextFieldValidation constructor(
    val textNotice: String,
    val validationLogic: () -> Boolean
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextFieldForm(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isLastTextFiled: Boolean = false,
    textFieldValidation: TextFieldValidation? = null
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        val focusRequester = remember { FocusRequester() }

        val keyboardController = LocalSoftwareKeyboardController.current

        val isPassword = keyboardType == KeyboardType.Password
        val (showPassword, toggleVisibility) = rememberSaveable {
            mutableStateOf(false)
        }

        var nextChanged by remember {
            mutableStateOf(false)
        }

        var hasFocus by remember {
            mutableStateOf(false)
        }

        val isValid = (textFieldValidation?.validationLogic?.let { it() } ?: true)

        val textColor = if (isValid) {
            MaterialTheme.colorScheme.surface
        } else if (nextChanged) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        }

        val fieldColor = if (isValid) {
            MaterialTheme.colorScheme.onSurface
        } else if (nextChanged) {
            MaterialTheme.colorScheme.primary.copy(.2f)
        } else {
            MaterialTheme.colorScheme.onSurface
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                nextChanged = false
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = if (isLastTextFiled) ImeAction.Done else ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    nextChanged = true
                    keyboardController?.hide()
                },
                onNext = {
                    nextChanged = true
                    defaultKeyboardAction(ImeAction.Next)
                }
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.hasFocus) {
                        hasFocus = true
                    }
                    if (hasFocus && !focusState.hasFocus) {
                        nextChanged = true
                        hasFocus = false
                    }
                },
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = fieldColor,
                            shape = RoundedCornerShape(25)
                        )
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.surface.copy(
                                        .2f
                                    )
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        if (isPassword) {
                            Icon(
                                painter = visibilityIcon(showPassword),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        toggleVisibility(!showPassword)
                                    }
                                    .size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    it()
                }

            }
        )


        if (!isValid && nextChanged) {
            Text(
                text = textFieldValidation?.textNotice ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

@Composable
fun ProfileTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    border: BorderStroke? = null
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        border = border,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TextFooter(modifier: Modifier, textLabel: String, textAction: String, actionClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = textLabel,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Black,
            modifier = Modifier,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = actionClick) {
            Text(
                text = textAction,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
fun ButtonForm(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(.2f),
            disabledContentColor = MaterialTheme.colorScheme.surface.copy(.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }

    }
}

@Composable
fun visibilityIcon(isVisible: Boolean) =
    painterResource(id = if (isVisible) R.drawable.round_visibility_off_24 else R.drawable.round_visibility_24)

@Preview
@Composable
fun PreviewFields() {
    TextFieldForm(
        title = "Nome",
        value = "",
        onValueChange = {},
        placeholder = "Digite o nome"
    )
}

@Preview
@Composable
fun PreviewButton() {
    ButtonForm(
        onClick = {

        },
        text = stringResource(id = R.string.create_new_account),
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
    )
}