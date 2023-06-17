package com.jalloft.michat.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.jalloft.michat.R
import com.jalloft.michat.data.firebase.FirebaseReauthenticate
import com.jalloft.michat.ui.components.*
import com.jalloft.michat.ui.theme.MichatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDataScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
    onReauthenticate: (FirebaseReauthenticate) -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.edit_data),
                onBackClicked = onBackClicked,
            )
        },
    ) { values ->
        var name by remember { mutableStateOf(authViewModel.getUser()?.displayName ?: "") }
        var email by remember { mutableStateOf(authViewModel.getUser()?.email ?: "") }

        val isValidFields =
            isValidEmail(email) && isTextValid(name) && (name != authViewModel.getUser()?.displayName || email != authViewModel.getUser()?.email)

//        val updateUserDataState by remember {
//            mutableStateOf(authViewModel.editUserDataState.value)
//        }

        val context = LocalContext.current

        val updateUserDataState by authViewModel.editUserDataState.observeAsState()
        var loadingUpdateUserData by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .padding(values)
                .padding(16.dp)
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
                modifier = Modifier
                    .padding(bottom = 16.dp),
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

            ChangePassword(
                modifier = Modifier
                    .fillMaxWidth()
            )

            ButtonForm(
                onClick = { authViewModel.editUserData(name, email) },
                text = stringResource(id = R.string.save_editions),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = isValidFields,
//                isLoading = updateUserDataState?.
            )
        }

//        when (val state = updateUserDataState) {
//            is AuthViewModel.UpdateUserDataState.Loading -> LaunchedEffect(updateUserDataState) {}
//            is AuthViewModel.UpdateUserDataState.Sucess -> LaunchedEffect(updateUserDataState) {}
//            is AuthViewModel.UpdateUserDataState.Failure -> LaunchedEffect(updateUserDataState) {}
//            else -> {}
//        }

        LaunchedEffect(updateUserDataState) {
            when (val state = updateUserDataState) {
                is AuthViewModel.UpdateUserDataState.Loading -> {
                    loadingUpdateUserData = true
                }
                is AuthViewModel.UpdateUserDataState.Sucess -> onSuccess()
                is AuthViewModel.UpdateUserDataState.Failure -> {
                    if (state.exception is FirebaseAuthRecentLoginRequiredException) {
                        onReauthenticate(FirebaseReauthenticate.EMAIL)
                    }else{
                        Toast.makeText(context, R.string.an_error_occurred_while_updating_the_data, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }


    }
}

@Composable
fun ChangePassword(modifier: Modifier) {
    Column(
        modifier = modifier.clickable { }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface)
                .height(1.dp)
        )
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.change_password),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_indicator),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface)
                .height(1.dp)
        )
    }
}


@Preview
@Composable
fun PreviewEditUserDataScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EditUserDataScreen()
        }
    }
}
