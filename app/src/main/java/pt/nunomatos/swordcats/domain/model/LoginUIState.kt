package pt.nunomatos.swordcats.domain.model

data class LoginUIState(
    val email: String = "",
    val name: String = "",
    val createAccount: Boolean = false
)