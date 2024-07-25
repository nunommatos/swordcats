package pt.nunomatos.swordcats.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.UserFeedModel
import retrofit2.Response

interface ICatsRepository {

    fun getCats(initialRequest: Boolean): (suspend () -> Response<List<CatModel>>)

    fun getFavoriteCats(cats: List<CatModel>): (suspend () -> Response<List<FavoriteCatModel>>)

    fun hasLocalFeed(): Boolean

    fun addCatAsFavorite(catId: String): (suspend () -> Response<FavoriteCatModel>)

    fun removeCatAsFavorite(catId: String): (suspend () -> Response<Void>)

    suspend fun updateFavoriteCat(favoriteCat: FavoriteCatModel)

    suspend fun updateUserFeed(cats: List<CatModel>, initialRequest: Boolean)

    fun updateFavoriteCats(favoriteCatsList: List<FavoriteCatModel>)

    fun listenToLocalFeedFlow(): Flow<UserFeedModel>
}