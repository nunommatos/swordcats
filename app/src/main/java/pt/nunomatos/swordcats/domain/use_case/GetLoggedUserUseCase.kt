package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Inject

class GetLoggedUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<UserModel?> {
        return userRepository.listenToCurrentUser()
    }
}