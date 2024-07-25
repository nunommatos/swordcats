package pt.nunomatos.swordcats.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.nunomatos.swordcats.data.model.CatsRoute
import pt.nunomatos.swordcats.presentation.compose.CatsAppTheme
import pt.nunomatos.swordcats.presentation.ui.cats.CatsScreen1
import pt.nunomatos.swordcats.presentation.ui.cats.details.CatDetailsScreen
import pt.nunomatos.swordcats.presentation.ui.login.LoginScreen
import pt.nunomatos.swordcats.presentation.ui.splash.SplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            CatsAppTheme {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = CatsRoute.Splash.name,
                    builder = {
                        composable(CatsRoute.Splash.name) {
                            SplashScreen(
                                viewModel = hiltViewModel(),
                                navController = navController
                            )
                        }
                        composable(CatsRoute.Login.name) {
                            LoginScreen(
                                viewModel = hiltViewModel(),
                                navController = navController
                            )
                        }
                        composable(CatsRoute.Cats.name) {
                            CatsScreen1(
                                viewModel = hiltViewModel(),
                                navController = navController
                            )
                        }
                        composable(
                            route = CatsRoute.CatDetails.name
                        ) {
                            CatDetailsScreen(
                                viewModel = hiltViewModel(),
                                navController = navController
                            )
                        }
                    }
                )
            }
        }
    }
}