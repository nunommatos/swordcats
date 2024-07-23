package pt.nunomatos.swordcats.data.model

const val ARGUMENT_CAT_DETAILS_KEY = "cat"
const val ARGUMENT_CAT_DETAILS_VALUE = "{cat}"

sealed class CatsRoute(
    val name: String,
) {
    data object Splash : CatsRoute(name = "splash")
    data object Login : CatsRoute(name = "login")
    data object Cats : CatsRoute(name = "cats")
    data object CatDetails :
        CatsRoute(name = "catDetails?$ARGUMENT_CAT_DETAILS_KEY=$ARGUMENT_CAT_DETAILS_VALUE")
}