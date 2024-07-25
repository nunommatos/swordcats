package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import pt.nunomatos.swordcats.common.toApiResponseFlow
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class GetCatsUseCase @Inject constructor(
    private val catsRepository: ICatsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(initialRequest: Boolean): Flow<ApiResponse<*>> {
        return catsRepository.getCats(initialRequest)
            .toApiResponseFlow()
            .flatMapMerge { response ->
                if (response.isSuccess()) {
                    getFavoriteCats(
                        cats = response.data.orEmpty(),
                        initialRequest = initialRequest
                    )
                } else if (response.isNetworkError() && initialRequest) {
                    flow {
                        emit(
                            if (catsRepository.hasLocalFeed()) {
                                ApiResponse.Success(Any())
                            } else {
                                ApiResponse.Error.NetworkError
                            }
                        )
                    }
                } else {
                    flow { emit(response) }
                }
            }
    }

    private fun getFavoriteCats(
        cats: List<CatModel>,
        initialRequest: Boolean
    ): Flow<ApiResponse<*>> {
        return catsRepository.getFavoriteCats(cats)
            .toApiResponseFlow()
            .onEach {
                if (it.isSuccess()) {
                    catsRepository.updateFavoriteCats(favoriteCatsList = it.data.orEmpty())
                    catsRepository.updateUserFeed(
                        cats = cats,
                        initialRequest = initialRequest
                    )
                }
            }
    }
}