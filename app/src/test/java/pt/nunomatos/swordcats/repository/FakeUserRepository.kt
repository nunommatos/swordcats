package pt.nunomatos.swordcats.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository

class FakeUserRepository : IUserRepository {
    private val user = UserModel(id = "id", name = "Name", email = "email", userFeed = null)

    private val loggedUserFlow: MutableStateFlow<UserModel?> = MutableStateFlow(user)

    override suspend fun getUserWithEmail(email: String): UserModel? {
        return if (email.isNotBlank()) {
            user
        } else {
            null
        }
    }

    override fun listenToCurrentUser(): Flow<UserModel?> {
        return loggedUserFlow.asStateFlow()
    }

    override suspend fun logout() {
        delay(2000L)
        loggedUserFlow.emit(null)
    }

    override fun listenToLoginState(): Flow<LoginState> {
        return flow {
            emit(LoginState.Unknown)
            delay(1000L)
            emit(LoginState.LoggedIn)
            delay(1000L)
            emit(LoginState.LoggedOut)
        }
    }

    override suspend fun createUser(name: String, email: String) {
        loggedUserFlow.emit(
            UserModel(
                name = name,
                email = email
            )
        )
    }
}