package pt.nunomatos.swordcats.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.nunomatos.swordcats.R
import pt.nunomatos.swordcats.data.model.CatsRoute
import pt.nunomatos.swordcats.presentation.compose.AppBackground
import pt.nunomatos.swordcats.presentation.compose.LightGray
import pt.nunomatos.swordcats.presentation.compose.Raleway
import pt.nunomatos.swordcats.presentation.compose.SwordCatsButton
import pt.nunomatos.swordcats.presentation.compose.SwordCatsInputField

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController,
) {
    val loginUIState by viewModel.readLoginUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.readLoginCompletedFlow.collect { _ ->
            navController.navigate(CatsRoute.Cats.name) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Vertical))
            .padding(all = 16.dp),
        content = {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = R.drawable.ic_paw),
                        colorFilter = ColorFilter.tint(color = LightGray),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(id = R.string.app_name),
                        style = TextStyle(
                            fontFamily = Raleway,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 36.sp,
                            color = LightGray
                        )
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(
                            id = if (loginUIState.createAccount) {
                                R.string.login_screen_register_message
                            } else {
                                R.string.login_screen_login_message
                            }
                        ),
                        style = TextStyle(
                            fontFamily = Raleway,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = LightGray,
                            textAlign = TextAlign.Center
                        )
                    )
                    SwordCatsInputField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        value = if (loginUIState.createAccount) {
                            loginUIState.name
                        } else {
                            loginUIState.email
                        },
                        onValueChanged = {
                            if (loginUIState.createAccount) {
                                viewModel.updateLoginName(it)
                            } else {
                                viewModel.updateLoginEmail(it)
                            }
                        },
                        hintRes = if (loginUIState.createAccount) {
                            R.string.input_hint_name
                        } else {
                            R.string.input_hint_email
                        }
                    )
                }
            )

            if (loginUIState.createAccount) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 16.dp),
                    content = {
                        SwordCatsButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = loginUIState.name.isNotBlank(),
                            textRes = R.string.login_screen_register,
                            onClick = {
                                viewModel.registerUser()
                            }
                        )
                        SwordCatsButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            textRes = R.string.login_screen_cancel,
                            onClick = {
                                viewModel.cancelRegistration()
                            }
                        )
                    }
                )

            } else {
                SwordCatsButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 16.dp),
                    enabled = loginUIState.email.isNotBlank(),
                    textRes = R.string.login_screen_login,
                    onClick = {
                        viewModel.login()
                    }
                )
            }
        }
    )
}