package pt.nunomatos.swordcats.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import pt.nunomatos.swordcats.data.model.CatBreedModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.CatWeightModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import retrofit2.Response

class FakeCatsRepository : ICatsRepository {

    private val breed1 = CatBreedModel(
        name = "breed1",
        lifeSpan = "2 - 3",
        weight = CatWeightModel(metric = "4 - 5"),
        temperament = "Calm, Peaceful",
        countryCode = "PT",
        origin = "Portugal",
        description = "Awesome cat",
        detailsUrl = null
    )

    private val breed2 = CatBreedModel(
        name = "breed2",
        lifeSpan = "4 - 5",
        weight = CatWeightModel(metric = "7 - 9"),
        temperament = "Patient, Friendly",
        countryCode = "PT",
        origin = "Portugal",
        description = "Awesome cat",
        detailsUrl = null
    )

    private val breed3 = CatBreedModel(
        name = "breed3",
        lifeSpan = "1 - 2",
        weight = CatWeightModel(metric = "10 - 11"),
        temperament = "Agressive, Silly",
        countryCode = "PT",
        origin = "Portugal",
        description = "Awesome cat",
        detailsUrl = null
    )

    private val favoriteCats = arrayListOf<FavoriteCatModel>()

    override fun hasLocalFeed(): Boolean {
        return false
    }

    override fun listenToLocalFeedFlow(): Flow<UserFeedModel> {
        return flow {
            emit(UserFeedModel(feedPage = 0, updatedAt = 0L, cats = listOf()))
            delay(2000L)
            emit(
                UserFeedModel(
                    feedPage = 1,
                    updatedAt = 1L,
                    cats = listOf(
                        CatModel()
                    )
                )
            )
        }
    }

    override fun getCats(initialRequest: Boolean): suspend () -> Response<List<CatModel>> {
        return suspend {
            delay(2000L)
            if (initialRequest) {
                Response.success(
                    listOf(
                        CatModel(id = "id1", favoriteId = "", breeds = listOf(breed1)),
                        CatModel(id = "id2", favoriteId = "", breeds = listOf(breed2)),
                        CatModel(id = "id3", favoriteId = "", breeds = listOf(breed3)),
                    )
                )
            } else {
                Response.error(400, "".toResponseBody())
            }
        }
    }

    override fun getFavoriteCats(cats: List<CatModel>): suspend () -> Response<List<FavoriteCatModel>> {
        return suspend {
            delay(2000L)
            favoriteCats.add(FavoriteCatModel(id = "fId1", catId = "id1"))
            Response.success(favoriteCats)
        }
    }

    override fun addCatAsFavorite(catId: String): suspend () -> Response<FavoriteCatModel> {
        return suspend {
            delay(2000L)
            Response.success(FavoriteCatModel("fId2", catId))
        }
    }

    override fun removeCatAsFavorite(catId: String): suspend () -> Response<Void> {
        return suspend {
            delay(2000L)
            Response.success(null)
        }
    }

    override suspend fun updateFavoriteCat(favoriteCat: FavoriteCatModel) {
        // do nothing
    }

    override suspend fun updateUserFeed(cats: List<CatModel>, initialRequest: Boolean) {
        // do nothing
    }

    override fun updateFavoriteCats(favoriteCatsList: List<FavoriteCatModel>) {
        // do nothing
    }
}