package pt.nunomatos.swordcats.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject

class UpdateFavoriteCatStateUseCase @Inject constructor(
    private val catsRepository: ICatsRepository
) {
    operator fun invoke(
        catId: String,
        isFavorite: Boolean
    ): Flow<ApiResponseModel<FavoriteCatModel>> {
        return if (isFavorite) {
            catsRepository.removeCatAsFavorite(catId)
        } else {
            catsRepository.addCatAsFavorite(catId)
        }.onEach {
            if (it.isSuccess()) {
                it.data?.let { favoriteCat ->
                    catsRepository.updateFavoriteCat(favoriteCat)
                }
            }
        }
    }
}