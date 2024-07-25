package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.data.model.FavoriteCatModel
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import pt.nunomatos.swordcats.repository.FakeCatsRepository

class UpdateFavoriteCatStateUseCaseTest {
    private lateinit var catsRepository: FakeCatsRepository
    private lateinit var updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase

    @Before
    fun setUp() {
        catsRepository = FakeCatsRepository()
        updateFavoriteCatStateUseCase = UpdateFavoriteCatStateUseCase(catsRepository)
    }

    @Test
    fun `Add Cat As Favorite`() {
        runTest {
            val responses = mutableListOf<ApiResponse<FavoriteCatModel>>()
            updateFavoriteCatStateUseCase.invoke("catId", false).toList(responses)
            assert(responses.size == 2)
            assert(responses.first().isLoading())
            val lastResponse = responses.last()
            assert(lastResponse.isSuccess())
            assert(lastResponse.data?.catId == "catId")
        }
    }

    @Test
    fun `Remove Favorite Cat`() {
        runTest {
            val responses = mutableListOf<ApiResponse<FavoriteCatModel>>()
            updateFavoriteCatStateUseCase.invoke("catId", true).toList(responses)
            assert(responses.size == 2)
            assert(responses.first().isLoading())
            val lastResponse = responses.last()
            assert(lastResponse.isSuccess())
            assert(lastResponse.data?.id.isNullOrBlank())
        }
    }
}