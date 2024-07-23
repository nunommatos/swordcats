package pt.nunomatos.swordcats.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.UserFeedModel

interface ICatsRepository {

    fun getCatBreeds(page: Int): Flow<ApiResponseModel<List<CatModel>>>

    fun addCatAsFavorite(catId: String): Flow<ApiResponseModel<FavoriteCatModel>>

    fun removeCatAsFavorite(catId: String): Flow<ApiResponseModel<FavoriteCatModel>>

    fun updateFavoriteCat(favoriteCat: FavoriteCatModel)

    fun resetInformation()

    fun listenToUserFeedFlow(): StateFlow<UserFeedModel?>
}