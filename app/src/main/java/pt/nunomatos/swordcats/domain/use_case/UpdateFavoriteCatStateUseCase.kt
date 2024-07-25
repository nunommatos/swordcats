package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import pt.nunomatos.swordcats.common.toApiResponseFlow
import pt.nunomatos.swordcats.common.toNullableApiResponseFlow
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class UpdateFavoriteCatStateUseCase @Inject constructor(
    private val catsRepository: ICatsRepository
) {
    operator fun invoke(
        catId: String,
        isFavorite: Boolean
    ): Flow<ApiResponse<FavoriteCatModel>> {
        return if (isFavorite) {
            removeFavoriteCat(catId)
        } else {
            addFavoriteCat(catId)
        }
    }

    private fun addFavoriteCat(catId: String): Flow<ApiResponse<FavoriteCatModel>> {
        return catsRepository.addCatAsFavorite(catId)
            .toApiResponseFlow()
            .onEach { response ->
                if (response.isSuccess()) {
                    response.data?.let { favoriteCat ->
                        catsRepository.updateFavoriteCat(
                            FavoriteCatModel(
                                id = favoriteCat.id,
                                catId = catId
                            )
                        )
                    }
                }
            }
    }

    private fun removeFavoriteCat(catId: String): Flow<ApiResponse<FavoriteCatModel>> {
        return catsRepository.removeCatAsFavorite(catId)
            .toNullableApiResponseFlow()
            .map { response ->
                if (response.isSuccess()) {
                    val removedFavoriteCat = FavoriteCatModel(id = "", catId = catId)
                    catsRepository.updateFavoriteCat(removedFavoriteCat)
                    ApiResponse.Success(removedFavoriteCat)
                } else if (response.isLoading()) {
                    ApiResponse.Loading
                } else if (response.isGenericError()) {
                    ApiResponse.Error.GenericError
                } else if (response.isNetworkError()) {
                    ApiResponse.Error.NetworkError
                } else {
                    ApiResponse.Start
                }
            }
    }
}