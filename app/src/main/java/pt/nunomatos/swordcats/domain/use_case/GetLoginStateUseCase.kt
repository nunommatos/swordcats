package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Inject

class GetLoginStateUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<LoginState> {
        return userRepository.listenToLoginState()
    }
}