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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import pt.nunomatos.swordcats.data.model.ARGUMENT_CAT_DETAILS_KEY
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.CatsRoute
import pt.nunomatos.swordcats.presentation.ui.cats.CatsScreen
import pt.nunomatos.swordcats.presentation.ui.cats.details.CatDetailsScreen
import pt.nunomatos.swordcats.presentation.compose.CatsAppTheme
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
                            CatsScreen(
                                viewModel = hiltViewModel(),
                                navController = navController
                            )
                        }
                        composable(
                            route = CatsRoute.CatDetails.name
                        ) {
                            val catStr = it.arguments?.getString(ARGUMENT_CAT_DETAILS_KEY).orEmpty()
                            val cat = Gson().fromJson(catStr, CatModel::class.java)
                            CatDetailsScreen(
                                cat = cat,
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