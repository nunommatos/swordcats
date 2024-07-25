package pt.nunomatos.swordcats.presentation.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import pt.nunomatos.swordcats.data.model.CatsRoute
import pt.nunomatos.swordcats.data.model.LoginState

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.readUserLoggedInFlow.collect { state ->
            if (state != LoginState.Unknown) {
                val route = if (state.isLoggedIn()) {
                    CatsRoute.Cats
                } else {
                    CatsRoute.Login
                }
                navController.navigate(route.name) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }
    }
}