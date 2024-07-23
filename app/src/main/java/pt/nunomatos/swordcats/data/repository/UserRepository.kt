package pt.nunomatos.swordcats.data.repository

import kotlinx.coroutines.flow.StateFlow
import pt.nunomatos.swordcats.data.local.LocalDataSource
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) : IUserRepository {

    override suspend fun getUserWithEmail(email: String): UserModel? {
        return localDataSource.getUserWithEmail(email)
    }

    override suspend fun registerUser(name: String, email: String) {
        localDataSource.registerUser(
            user = UserModel(
                id = UUID.randomUUID().toString(),
                name = name,
                email = email
            )
        )
    }

    override suspend fun updateUserFeed(userFeed: UserFeedModel) {
        localDataSource.updateUserFeed(userFeed)
    }

    override suspend fun logout() {
        localDataSource.logout()
    }

    override fun listenToUserLoginState(): StateFlow<LoginState> {
        return localDataSource.readLoginStateFlow
    }
}