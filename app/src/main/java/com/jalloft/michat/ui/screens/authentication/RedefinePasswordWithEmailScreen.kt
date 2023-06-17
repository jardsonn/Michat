package com.jalloft.michat.ui.screens.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
fun RedefinePasswordWithEmailScreen(
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
            var email by remember { mutableStateOf("") }

            val isValidFields = isValidEmail(email)

            Text(
                text = stringResource(R.string.redefine_password_message),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface.copy(.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TextFieldForm(
                title = stringResource(id = R.string.email),
                value = email,
                keyboardType = KeyboardType.Email,
                placeholder = stringResource(R.string.type_your_email),
                onValueChange = { email = it },
                isLastTextFiled = true,
                textFieldValidation = TextFieldValidation(
                    textNotice = if (email.isEmpty()) stringResource(R.string.please_fill_in_the_email_field) else stringResource(
                        R.string.please_enter_a_valid_email
                    ),
                    validationLogic = { isValidEmail(email) }
                )
            )

            ButtonForm(
                onClick = { /*TODO*/ },
                text = stringResource(id = R.string.send_email),
                modifier = Modifier
                    .fillMaxWidth().padding(top = 16.dp),
                enabled = isValidFields,
            )
        }
    }
}


@Preview
@Composable
fun PreviewRedefinePasswordWithEmailScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RedefinePasswordWithEmailScreen()
        }
    }
}
