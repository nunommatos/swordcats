package pt.nunomatos.swordcats.data.model

sealed class LoginState {
    data object Unknown : LoginState()
    data object LoggedIn : LoginState()
    data object LoggedOut : LoginState()

    fun isLoggedIn(): Boolean {
        return this is LoggedIn
    }

    fun isLoggedOut(): Boolean {
        return this is LoggedOut
    }

    fun isUnknown(): Boolean {
        return this is Unknown
    }
}