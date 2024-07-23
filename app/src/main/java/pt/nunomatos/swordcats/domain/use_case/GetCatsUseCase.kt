package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class GetCatsUseCase @Inject constructor(
    private val catsRepository: ICatsRepository,
) {
    operator fun invoke(page: Int): Flow<ApiResponseModel<*>> {
        return catsRepository.getCatBreeds(page)
    }
}