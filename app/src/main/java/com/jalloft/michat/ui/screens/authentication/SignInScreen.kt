package com.jalloft.michat.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalloft.michat.R
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.UserData
import com.jalloft.michat.data.firebase.FirebaseReauthenticate
import com.jalloft.michat.ui.components.*
import com.jalloft.michat.ui.theme.MichatTheme
import com.jalloft.michat.utils.Response
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    reauthenticate: FirebaseReauthenticate = FirebaseReauthenticate.NONE,
    onBackClicked: () -> Unit = {},
    onSignin: (AssistantIdentifier) -> Unit = {},
    onSignup: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onSigninWithGoogle: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.login),
                onBackClicked = onBackClicked,
            )
        },
    ) { values ->

        val signInEmailState = authViewModel.signInWithEmailAndPasswordState.observeAsState()

//        var showToast by remember {
//            mutableStateOf(false)
//        }

        val context = LocalContext.current
        when (val state = signInEmailState.value) {
            is Response.Success<UserData> -> {
                LaunchedEffect(signInEmailState) {
                    if (authViewModel.currentAssistant != null) {
                        onSignin(authViewModel.currentAssistant)
                    } else {
                        onBackClicked()
                    }
                }
            }
            is Response.Failure -> {
                Timber.i("Erro entrar na conta: ${state.errorMessage}")
                LaunchedEffect(signInEmailState) {
                    Toast.makeText(
                        context,
                        R.string.an_attempt_to_login_occurred,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {}
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .padding(16.dp)
        ) {
            val (emailRef, passwordRef, forgotPasswordRef, footerRef, loginNotice) = createRefs()
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            val isValidFields = isValidEmail(email) && isValidPassword(password)

            if (reauthenticate != FirebaseReauthenticate.NONE){
                Text(
                    text = stringResource(id = R.string.to_change_update_data_you_must_login_again),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(bottom = 8.dp, top = 16.dp)
                        .constrainAs(loginNotice) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
            }

            TextFieldForm(
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 16.dp)
                    .constrainAs(emailRef) {
                        top.linkTo(loginNotice.bottom)
                        start.linkTo(parent.start)
                    },
                title = stringResource(id = R.string.email),
                value = email,
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
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .constrainAs(passwordRef) {
                        top.linkTo(emailRef.bottom)
                        start.linkTo(parent.start)
                    },
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

            ProfileTextButton(
                onClick = onForgotPassword,
                text = stringResource(R.string.forgot_password),
                modifier = Modifier.constrainAs(forgotPasswordRef) {
                    top.linkTo(passwordRef.bottom)
                    end.linkTo(parent.end)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(footerRef) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {

                ButtonForm(
                    onClick = {
                        authViewModel.signInWithEmailAndPassword(email, password)
                    },
                    text = stringResource(id = R.string.login),
                    isLoading = signInEmailState.value is Response.Loading,
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = isValidFields,
                )
                LineOrLine(modifier = Modifier.padding(vertical = 16.dp))
                GoogleButton(onClick = onSigninWithGoogle, modifier = Modifier.fillMaxWidth())
                TextFooter(
                    modifier = Modifier,
                    textLabel = stringResource(R.string.new_in_michat),
                    textAction = stringResource(R.string.create_new_account),
                    actionClick = onSignup
                )
            }
        }
    }
}

@Composable
fun LineOrLine(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSecondary.copy(.5f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(id = R.string.or),
            color = MaterialTheme.colorScheme.onSecondary.copy(.5f),
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSecondary.copy(.5f),
            modifier = Modifier.weight(1f)
        )

    }
}


@Composable
fun GoogleButton(onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_goole),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(
                text = stringResource(id = R.string.login_with_google),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.surface.copy(.5f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SignInScreen()
        }
    }
}
