package pt.nunomatos.swordcats.domain.use_case

import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(email: String): UserModel? {
        return userRepository.getUserWithEmail(email)
    }
}