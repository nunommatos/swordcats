package pt.nunomatos.swordcats.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.data.local.LocalDataSource
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatRequestModel
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.data.remote.RemoteDataSource
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatsRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : ICatsRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val localFeedFlow = MutableSharedFlow<UserFeedModel>()

    private var favoriteCats: ArrayList<FavoriteCatModel>? = null
    private var currentUser: UserModel? = null

    init {
        coroutineScope.launch {
            localDataSource.listenToCurrentUser().collect {
                currentUser = it

                // the user logged out
                if (it == null) {
                    favoriteCats = null
                }
            }
        }
    }

    override fun hasLocalFeed(): Boolean {
        val hasLocalFeed = currentUser?.userFeed != null
        if (hasLocalFeed) {
            coroutineScope.launch {
                localFeedFlow.emit(currentUser?.userFeed!!)
            }
        }
        return hasLocalFeed
    }

    override fun getCats(initialRequest: Boolean): suspend () -> Response<List<CatModel>> {
        val page = if (!initialRequest) {
            currentUser?.userFeed?.feedPage ?: 0
        } else {
            0
        }

        return remoteDataSource.getCats(page)
    }

    override fun getFavoriteCats(cats: List<CatModel>): suspend () -> Response<List<FavoriteCatModel>> {
        return if (favoriteCats != null) {
            suspend { Response.success(favoriteCats) }
        } else {
            remoteDataSource.getFavoriteCatBreeds(
                userId = currentUser?.id.orEmpty()
            )
        }
    }

    override fun addCatAsFavorite(catId: String): suspend () -> Response<FavoriteCatModel> {
        return remoteDataSource.addCatAsFavorite(
            favoriteCat = FavoriteCatRequestModel(
                catId = catId,
                userId = currentUser?.id.orEmpty()
            )
        )
    }

    override fun removeCatAsFavorite(catId: String): suspend () -> Response<Void> {
        val favoriteCatToRemove = favoriteCats?.firstOrNull { it.catId == catId }
        return remoteDataSource.removeCatAsFavorite(favoriteCatToRemove?.id.orEmpty())
    }

    override fun updateFavoriteCats(favoriteCatsList: List<FavoriteCatModel>) {
        favoriteCats = ArrayList(favoriteCatsList)
    }

    override suspend fun updateUserFeed(cats: List<CatModel>, initialRequest: Boolean) {
        currentUser?.let {
            val updatedCats = updateFeedFavoriteCats(cats)

            val feedPage = if (initialRequest) {
                0
            } else {
                it.userFeed?.feedPage ?: 0
            }
            val updatedUser = it.copy(
                userFeed = UserFeedModel.fromNow(
                    feedPage = feedPage + 1,
                    cats = if (feedPage > 0) {
                        it.userFeed?.cats.orEmpty().toMutableList().apply {
                            addAll(updatedCats)
                        }
                    } else {
                        updatedCats
                    }
                )
            )
            currentUser = updatedUser
            localDataSource.updateUser(updatedUser)
        }
    }

    override suspend fun updateFavoriteCat(favoriteCat: FavoriteCatModel) {
        currentUser?.let {
            if (favoriteCat.id.isNotBlank()) {
                favoriteCats?.add(favoriteCat)
            } else {
                favoriteCats?.removeIf { it.catId == favoriteCat.catId }
            }

            val currentFeed = it.userFeed
            val currentCats = currentFeed?.cats.orEmpty()
            val updatedUser = it.copy(
                userFeed = UserFeedModel.fromNow(
                    feedPage = currentFeed?.feedPage ?: 0,
                    cats = updateFeedFavoriteCats(currentCats)
                )
            )
            currentUser = updatedUser
            localDataSource.updateUser(updatedUser)
        }
    }

    override fun listenToLocalFeedFlow(): Flow<UserFeedModel> {
        return localFeedFlow.asSharedFlow()
    }

    private fun updateFeedFavoriteCats(cats: List<CatModel>): List<CatModel> {
        return cats.map { cat ->
            cat.copy(
                favoriteId = favoriteCats?.firstOrNull { favoriteCat ->
                    favoriteCat.catId == cat.id
                }?.id
            )
        }
    }
}