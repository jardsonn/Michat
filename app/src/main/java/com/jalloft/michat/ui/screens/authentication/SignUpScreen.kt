package com.jalloft.michat.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalloft.michat.R
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.UserData
import com.jalloft.michat.ui.components.*
import com.jalloft.michat.ui.theme.MichatTheme
import com.jalloft.michat.utils.Response
import timber.log.Timber.Forest.i

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
    onSignin: (AssistantIdentifier?) -> Unit = {},
    onSuccessSignup: (AssistantIdentifier?) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.signup),
                onBackClicked = onBackClicked,
            )
        },
    ) { values ->

        val createUserState = authViewModel.createUserWithEmailAndPasswordState.observeAsState()
        val context = LocalContext.current
        when (val state = createUserState.value) {
            is Response.Success<UserData> ->
                LaunchedEffect(createUserState){
                    onSuccessSignup(authViewModel.currentAssistant)
                }
            is Response.Failure -> {
                i("Erro ao criar conta: ${state.errorMessage}")
                LaunchedEffect(createUserState){
                    Toast.makeText(context, R.string.there_was_an_error_creating_an_account, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .padding(16.dp)
        ) {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            val isValidFields =
                isValidEmail(email) && isValidPassword(password) && isTextValid(name)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                TextFieldForm(
                    modifier = Modifier.padding(top = 16.dp),
                    title = stringResource(id = R.string.name),
                    value = name,
                    placeholder = stringResource(R.string.type_your_name),
                    onValueChange = { name = it },
                    textFieldValidation = TextFieldValidation(
                        textNotice = if (name.isEmpty()) stringResource(R.string.please_fill_in_the_name_field) else stringResource(
                            R.string.please_enter_a_valid_name
                        ),
                        validationLogic = { isTextValid(name) }
                    )
                )
                TextFieldForm(
                    title = stringResource(id = R.string.email),
                    value = email,
                    keyboardType = KeyboardType.Email,
                    placeholder = stringResource(R.string.type_your_email),
                    onValueChange = { email = it },
                    textFieldValidation = TextFieldValidation(
                        textNotice = if (email.isEmpty()) stringResource(R.string.please_fill_in_the_email_field) else stringResource(
                            R.string.please_enter_a_valid_email
                        ),
                        validationLogic = { isValidEmail(email) }
                    )
                )
                TextFieldForm(
                    title = stringResource(id = R.string.password),
                    value = password,
                    placeholder = stringResource(R.string.type_your_password),
                    onValueChange = { password = it },
                    keyboardType = KeyboardType.Password,
                    isLastTextFiled = true,
                    textFieldValidation = TextFieldValidation(
                        textNotice = if (password.isEmpty()) stringResource(R.string.please_fill_in_the_password_field) else stringResource(
                            R.string.please_enter_a_valid_password
                        ),
                        validationLogic = { isValidPassword(password) }
                    )
                )
            }
            ButtonForm(
                onClick = {
                    authViewModel.createUserWithEmailAndPassword(name, email, password)
                },
                text = stringResource(id = R.string.create_new_account),
                modifier = Modifier.fillMaxWidth(),
                enabled = isValidFields,
                isLoading = createUserState.value is Response.Loading
            )
            TextFooter(
                modifier = Modifier,
                textLabel = stringResource(R.string.already_have_an_account),
                textAction = stringResource(R.string.login),
                actionClick = { onSignin(authViewModel.currentAssistant) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewSignUpScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SignUpScreen()
        }
    }
}
