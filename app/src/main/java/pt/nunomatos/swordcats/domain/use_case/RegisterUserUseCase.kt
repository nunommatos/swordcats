package pt.nunomatos.swordcats.domain.use_case

import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(name: String, email: String) {
        return userRepository.registerUser(name, email)
    }
}