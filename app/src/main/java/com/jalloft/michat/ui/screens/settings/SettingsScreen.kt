package com.jalloft.michat.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jalloft.michat.R
import com.jalloft.michat.ui.components.ScreensTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreensTopBar(
                title = stringResource(id = R.string.settings),
                onBackClicked = onBackClicked
            )
        },
    ) { values ->

        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(horizontal = 16.dp)
        ) {
            item {
                CardAds(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ })
                CardOption(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Option(
                        modifier =  Modifier.clickable(role = Role.Button) {  },
                        painter = painterResource(id = R.drawable.ic_my_account),
                        labelOption = "Minha conta",
                        labelDescription = "Configurações de sua conta",
                    )
                    Option(
                        modifier =  Modifier.clickable(role = Role.Button) {  },
                        painter = painterResource(id = R.drawable.ic_favorite),
                        labelOption = "Mensagens favoritas",
                        labelDescription = "Suas mensagens favoritas",
                    )

                    Option(
                        painter = painterResource(id = R.drawable.ic_sons),
                        labelOption = "Sons de conversas",
                        labelDescription = "Reproduzir sons ao receber mensagem",
                        indicador = {
                            Switch(checked = true, onCheckedChange = {})
                        },
                    )
                }

                CardOption(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Option(
                        modifier =  Modifier.clickable(role = Role.Button) {  },
                        painter = painterResource(id = R.drawable.ic_share),
                        labelOption = "Compartilhar aplicativo",
                        labelDescription = "Compartilhar aplicativo com amigos",
                    )
                    Option(
                        modifier =  Modifier.clickable(role = Role.Button) {  },
                        painter = painterResource(id = R.drawable.ic_feedback),
                        labelOption = "Feedback",
                        labelDescription = "Reporte bugs e conte-nos em que podemos melhorar",
                    )

                    Option(
                        modifier =  Modifier.clickable(role = Role.Button) {  },
                        painter = painterResource(id = R.drawable.ic_policy),
                        labelOption = "Política de Privacidade",
                        labelDescription = "Política de Privacidade",
                    )
                }
            }
        }

//        Column(
//            modifier = Modifier
//                .padding(values)
//                .padding(16.dp)
//        ) {
//
//        }
    }
}

@Composable
fun CardOption(modifier: Modifier, options: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary.copy(
                .1f
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            options()
        }
    }
}

@Composable
fun Option(
    modifier:Modifier = Modifier.fillMaxWidth(),
    painter: Painter,
    labelOption: String,
    labelDescription: String,
    indicador: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.ic_indicator),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(16.dp)
        )
    },
) {
    Box(
        modifier = modifier,
    ){
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter, contentDescription = null, tint = MaterialTheme.colorScheme.surface
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = labelOption,
                    style = MaterialTheme.typography.labelLarge,
//                modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.surface
                )
                Text(
                    text = labelDescription,
                    style = MaterialTheme.typography.labelMedium,
//                modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            indicador()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAds(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ads", style = MaterialTheme.typography.titleLarge)
        }
    }
}
//
//@Preview
//@Composable
//fun PreviewGreeting() {
//    SettingsScreen()
////    ScreensTopBar(title = stringResource(id = R.string.app_name))
//}


enum class SettingsOptions {
    MY_ACCOUNT, FAVORITE_MESSAGES, SOUNDS_OF_CONVERSATIONS, SHARE_APP, FEEDBACK, PRIVACY_POLICY
}
