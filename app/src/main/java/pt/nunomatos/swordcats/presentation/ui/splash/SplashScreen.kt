package pt.nunomatos.swordcats.presentation.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import pt.nunomatos.swordcats.data.model.CatsRoute

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.loginState.collect { state ->
            if (!state.isUnknown()) {
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