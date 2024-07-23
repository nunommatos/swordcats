package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.StateFlow
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class GetUserFeedUseCase @Inject constructor(
    private val catsRepository: ICatsRepository,
) {
    operator fun invoke(): StateFlow<UserFeedModel?> {
        return catsRepository.listenToUserFeedFlow()
    }
}