package pt.nunomatos.swordcats.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserModel

interface IUserRepository {

    suspend fun createUser(name: String, email: String)

    suspend fun getUserWithEmail(email: String): UserModel?

    suspend fun logout()

    fun listenToCurrentUser(): Flow<UserModel?>

    fun listenToLoginState(): Flow<LoginState>
}