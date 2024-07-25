package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class GetLocalFeedUseCase @Inject constructor(
    private val catsRepository: ICatsRepository
) {
    operator fun invoke(): Flow<UserFeedModel> {
        return catsRepository.listenToLocalFeedFlow()
    }
}