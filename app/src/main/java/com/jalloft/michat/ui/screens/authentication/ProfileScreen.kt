package com.jalloft.michat.ui.screens.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalloft.michat.R
import com.jalloft.michat.ui.MichatApp
import com.jalloft.michat.ui.components.ProfileTextButton
import com.jalloft.michat.ui.components.ScreensTopBar
import com.jalloft.michat.ui.theme.MichatTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.my_account),
                onBackClicked = onBackClicked,
                actions = {
                    ProfileTextButton(
                        onClick = onEditClicked,
                        text = stringResource(R.string.edit),
                    )
                }
            )
        },
    ) { values ->

        Column(
            modifier = Modifier
                .padding(values)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                UserInformation(
                    modifier = Modifier.padding(bottom = 16.dp),
                    title = stringResource(R.string.name),
                    information = authViewModel.getUser()?.displayName
                        ?: stringResource(R.string.name_not_defined)
                )

                UserInformation(
                    modifier = Modifier.padding(bottom = 16.dp),
                    title = stringResource(R.string.email),
                    information = authViewModel.getUser()?.email
                        ?: stringResource(R.string.name_not_defined)
                )

            }

            ProfileTextButton(
                onClick = {
                    authViewModel.signOut()
                    onBackClicked()
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                text = stringResource(R.string.exit),
            )
        }

    }
}


@Composable
fun UserInformation(modifier: Modifier = Modifier, title: String, information: String) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface.copy(.7f)
        )

        Text(
            text = information,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }

}

@Preview()
@Composable
fun PreviewProfileScreen() {
    MichatTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen()
        }
    }
}