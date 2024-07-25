package pt.nunomatos.swordcats.data.repository

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.local.LocalDataSource
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) : IUserRepository {

    override fun listenToCurrentUser(): Flow<UserModel?> {
        return localDataSource.listenToCurrentUser()
    }

    override fun listenToLoginState(): Flow<LoginState> {
        return localDataSource.listenToLoginState()
    }

    override suspend fun createUser(name: String, email: String) {
        localDataSource.createUser(
            user = UserModel(
                id = UUID.randomUUID().toString(),
                name = name,
                email = email
            )
        )
    }

    override suspend fun getUserWithEmail(email: String): UserModel? {
        return localDataSource.getUserWithEmail(email)
    }

    override suspend fun logout() {
        localDataSource.logout()
    }
}