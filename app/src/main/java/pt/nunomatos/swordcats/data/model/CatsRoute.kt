package pt.nunomatos.swordcats.data.model

import pt.nunomatos.swordcats.common.Constants.Keys.ARGUMENT_CAT_DETAILS_KEY
import pt.nunomatos.swordcats.common.Constants.Keys.ARGUMENT_CAT_DETAILS_VALUE

sealed class CatsRoute(
    val name: String,
) {
    data object Splash : CatsRoute(name = "splash")
    data object Login : CatsRoute(name = "login")
    data object Cats : CatsRoute(name = "cats")
    data object CatDetails :
        CatsRoute(name = "catDetails?$ARGUMENT_CAT_DETAILS_KEY=$ARGUMENT_CAT_DETAILS_VALUE")
}