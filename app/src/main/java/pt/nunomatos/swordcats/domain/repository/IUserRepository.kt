package pt.nunomatos.swordcats.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.data.model.UserModel

interface IUserRepository {
    suspend fun getUserWithEmail(email: String): UserModel?

    suspend fun registerUser(name: String, email: String)

    suspend fun updateUserFeed(userFeed: UserFeedModel)

    suspend fun logout()

    fun listenToUserLoginState(): StateFlow<LoginState>
}