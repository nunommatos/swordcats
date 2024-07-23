package pt.nunomatos.swordcats.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import pt.nunomatos.swordcats.common.toApiResponseFlow
import pt.nunomatos.swordcats.common.toNullableApiResponseFlow
import pt.nunomatos.swordcats.data.local.LocalDataSource
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatRequestModel
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.data.remote.RemoteDataSource
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatsRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : ICatsRepository {
    private var favoriteCatBreeds: ArrayList<FavoriteCatModel>? = null

    private val userFeedFlow: MutableStateFlow<UserFeedModel?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCatBreeds(page: Int): Flow<ApiResponseModel<List<CatModel>>> {
        return remoteDataSource.getCatBreeds(page)
            .toApiResponseFlow()
            .flatMapMerge { catsResponse ->
                when {
                    catsResponse.isSuccess() -> {
                        val cats = catsResponse.data.orEmpty()

                        if (favoriteCatBreeds != null) {
                            updateFeed(page = page, cats = cats)
                            flow { emit(ApiResponseModel.Success(cats)) }
                        } else {
                            val userId = localDataSource.readCurrentUserFlow.value?.id
                            remoteDataSource.getFavoriteCatBreeds(userId.orEmpty())
                                .toApiResponseFlow()
                                .map { response ->
                                    when {
                                        response.isSuccess() -> {
                                            favoriteCatBreeds =
                                                ArrayList(
                                                    response.data.orEmpty().map { favoriteCat ->
                                                        FavoriteCatModel(
                                                            id = favoriteCat.id,
                                                            catId = favoriteCat.catId
                                                        )
                                                    }
                                                )

                                            updateFeed(page = page, cats = cats)
                                            ApiResponseModel.Success(cats)
                                        }

                                        response.isError() -> {
                                            if (response.isNetworkError()) {
                                                ApiResponseModel.Error.NetworkError
                                            } else {
                                                ApiResponseModel.Error.GenericError
                                            }
                                        }

                                        else -> {
                                            ApiResponseModel.Loading
                                        }
                                    }
                                }
                        }
                    }

                    catsResponse.isError() -> {
                        if (catsResponse.isNetworkError()) {
                            val localFeed =
                                localDataSource.readCurrentUserFlow.value?.userFeed?.copy(
                                    isLocalFeed = true
                                )

                            // if the local feed's page is greater or equal than the request page
                            // it means it's already been retrieved and saved, so we can display
                            // the local version of the feed
                            if (localFeed != null && localFeed.feedPage >= page) {
                                userFeedFlow.value = localFeed
                                flow { emit(ApiResponseModel.Success(localFeed.cats)) }
                            } else {
                                flow { emit(catsResponse) }
                            }
                        } else {
                            flow { emit(catsResponse) }
                        }
                    }

                    else -> {
                        flow { emit(ApiResponseModel.Loading) }
                    }
                }
            }
    }

    override fun addCatAsFavorite(catId: String): Flow<ApiResponseModel<FavoriteCatModel>> {
        val userId = localDataSource.readCurrentUserFlow.value?.id
        return remoteDataSource.addCatAsFavorite(
            FavoriteCatRequestModel(
                catId = catId,
                userId = userId.orEmpty()
            )
        )
            .toApiResponseFlow()
            .map { response ->
                when {
                    response.isSuccess() -> {
                        val favoriteCat = FavoriteCatModel(
                            id = response.data?.id.orEmpty(),
                            catId = catId,
                        )
                        favoriteCatBreeds?.add(favoriteCat)
                        ApiResponseModel.Success(responseData = favoriteCat)
                    }

                    response.isError() -> {
                        response
                    }

                    else -> {
                        ApiResponseModel.Loading
                    }
                }
            }
    }

    override fun removeCatAsFavorite(catId: String): Flow<ApiResponseModel<FavoriteCatModel>> {
        val catToRemove = favoriteCatBreeds?.firstOrNull { it.catId == catId }
        return remoteDataSource.removeCatAsFavorite(
            catId = catToRemove?.id.orEmpty()
        )
            .toNullableApiResponseFlow()
            .map { response ->
                when {
                    response.isSuccess() -> {
                        favoriteCatBreeds?.remove(catToRemove)
                        ApiResponseModel.Success(
                            responseData = FavoriteCatModel(
                                id = "",
                                catId = catId
                            )
                        )
                    }

                    response.isError() -> {
                        if (response.isNetworkError()) {
                            ApiResponseModel.Error.NetworkError
                        } else {
                            ApiResponseModel.Error.GenericError
                        }
                    }

                    else -> {
                        ApiResponseModel.Loading
                    }
                }
            }
    }

    override fun updateFavoriteCat(favoriteCat: FavoriteCatModel) {
        userFeedFlow.update { userFeed ->
            userFeed?.copy(
                cats = userFeed.cats.map { cat ->
                    if (cat.id == favoriteCat.catId) {
                        cat.copy(favoriteId = favoriteCat.id)
                    } else {
                        cat
                    }
                }
            )
        }
    }

    override fun resetInformation() {
        favoriteCatBreeds = null
        userFeedFlow.value = null
    }

    override fun listenToUserFeedFlow(): StateFlow<UserFeedModel?> {
        return userFeedFlow
    }

    private fun updateFavoriteState(cats: List<CatModel>): List<CatModel> {
        return cats.map { cat ->
            cat.copy(
                favoriteId = favoriteCatBreeds?.firstOrNull { favoriteCat ->
                    favoriteCat.catId == cat.id
                }?.id
            )
        }
    }

    private fun updateFeed(page: Int, cats: List<CatModel>) {
        val catsList = updateFavoriteState(cats)
        userFeedFlow.update { currentFeed ->
            UserFeedModel.fromNow(
                feedPage = page,
                cats = if (page > 0) {
                    ArrayList(currentFeed?.cats.orEmpty()).apply {
                        addAll(catsList)
                    }
                } else {
                    catsList
                },
                isLocalFeed = false
            )
        }
    }
}