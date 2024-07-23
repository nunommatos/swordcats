package pt.nunomatos.swordcats.data.remote

import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.data.model.FavoriteCatRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CatsService {

    @GET("images/search?order=asc")
    suspend fun getCats(
        @Query("limit") limit: Int,
        @Query("has_breeds") hasBreeds: Int,
        @Query("page") page: Int
    ): Response<List<CatModel>>

    @POST("favourites")
    suspend fun addCatAsFavorite(@Body body: FavoriteCatRequestModel): Response<FavoriteCatModel>

    @DELETE("favourites/{favouriteId}")
    suspend fun removeCatAsFavorite(@Path("favouriteId") id: String): Response<Void>

    @GET("favourites?order=desc")
    suspend fun getFavoriteCats(@Query("sub_id") userId: String): Response<List<FavoriteCatModel>>

}