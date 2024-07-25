package pt.nunomatos.swordcats.data.remote

import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatRequestModel
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val catsService: CatsService
) {
    companion object {
        private const val REQUEST_LIMIT = 20
        private const val REQUEST_HAS_BREEDS = 1
    }

    fun getCats(page: Int): suspend () -> Response<List<CatModel>> {
        return suspend {
            catsService.getCats(
                limit = REQUEST_LIMIT,
                hasBreeds = REQUEST_HAS_BREEDS,
                page = page
            )
        }
    }

    fun getFavoriteCatBreeds(userId: String): suspend () -> Response<List<FavoriteCatModel>> {
        return suspend { catsService.getFavoriteCats(userId) }
    }

    fun addCatAsFavorite(
        favoriteCat: FavoriteCatRequestModel
    ): (suspend () -> Response<FavoriteCatModel>) {
        return suspend { catsService.addCatAsFavorite(favoriteCat) }
    }

    fun removeCatAsFavorite(catId: String): (suspend () -> Response<Void>) {
        return suspend { catsService.removeCatAsFavorite(catId) }
    }
}