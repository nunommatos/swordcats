package pt.nunomatos.swordcats.data.model

sealed class LoginState(val user: UserModel?) {
    data object Unknown : LoginState(user = null)
    data class LoggedIn(val loggedUser: UserModel) : LoginState(user = loggedUser)
    data object LoggedOut : LoginState(user = null)

    fun isLoggedIn(): Boolean {
        return this is LoggedIn
    }

    fun isUnknown(): Boolean {
        return this is Unknown
    }
}