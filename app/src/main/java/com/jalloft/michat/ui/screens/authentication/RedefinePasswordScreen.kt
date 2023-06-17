package com.jalloft.michat.ui.screens.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalloft.michat.R
import com.jalloft.michat.ui.components.*
import com.jalloft.michat.ui.theme.MichatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedefinePasswordScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.redefine_password),
                onBackClicked = onBackClicked,
            )
        },
    ) { values ->
        Column(
            modifier = Modifier.fillMaxSize().padding(values).padding(16.dp)
        ) {
            var newPassword by remember { mutableStateOf("") }
            var newPasswordRepetition by remember { mutableStateOf("") }

            val isValidFields = isValidPassword(newPassword) && newPassword == newPasswordRepetition

            TextFieldForm(
                title = stringResource(id = R.string.new_password),
                value = newPassword,
                placeholder = stringResource(R.string.type_your_new_password),
                onValueChange = { newPassword = it },
                keyboardType = KeyboardType.Password,
                isLastTextFiled = true,
                textFieldValidation = TextFieldValidation(
                    textNotice = if (newPassword.isEmpty()) stringResource(R.string.please_fill_in_the_password_field) else stringResource(
                        R.string.please_enter_a_valid_password
                    ),
                    validationLogic = { isValidPassword(newPassword) }
                )
            )
            TextFieldForm(
                title = stringResource(id = R.string.reapet_new_password),
                value = newPasswordRepetition,
                placeholder = stringResource(R.string.type_your_new_password),
                onValueChange = { newPasswordRepetition = it },
                keyboardType = KeyboardType.Password,
                isLastTextFiled = true,
                textFieldValidation = TextFieldValidation(
                    textNotice = if (newPasswordRepetition.isEmpty()) stringResource(R.string.please_fill_in_the_password_field) else stringResource(
                        R.string.passwords_entered_do_not_match
                    ),
                    validationLogic = { newPasswordRepetition == newPassword }
                )
            )

            ButtonForm(
                onClick = { /*TODO*/ },
                text = stringResource(id = R.string.login),
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isValidFields,
            )
        }
    }
}


@Preview
@Composable
fun PreviewRedefinePasswordScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RedefinePasswordScreen()
        }
    }
}
