package pt.nunomatos.swordcats.domain.use_case

import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Inject

class UpdateUserFeedUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userFeed: UserFeedModel) {
        userRepository.updateUserFeed(userFeed)
    }
}