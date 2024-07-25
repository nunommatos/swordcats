package pt.nunomatos.swordcats.view_model

import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import pt.nunomatos.swordcats.CoroutineTestRule
import pt.nunomatos.swordcats.common.Constants.Keys.ARGUMENT_CAT_DETAILS_KEY
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import pt.nunomatos.swordcats.presentation.ui.cats.details.CatDetailsViewModel
import pt.nunomatos.swordcats.repository.FakeCatsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CatDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule = CoroutineTestRule(testDispatcher)

    private lateinit var catDetailsViewModel: CatDetailsViewModel
    private lateinit var fakeCatsRepository: ICatsRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase

    private val cat = CatModel(id = "mock")

    @Before
    fun setUp() {
        savedStateHandle = Mockito.mock(SavedStateHandle::class.java)
        Mockito.`when`(savedStateHandle.get<String>(ARGUMENT_CAT_DETAILS_KEY).orEmpty())
            .thenReturn(Gson().toJson(cat))
        fakeCatsRepository = FakeCatsRepository()
        updateFavoriteCatStateUseCase = UpdateFavoriteCatStateUseCase(fakeCatsRepository)
        catDetailsViewModel = CatDetailsViewModel(savedStateHandle, updateFavoriteCatStateUseCase)
    }

    @Test
    fun `Add Favorite Cat`() {
        runTest {
            val catFlowResults = mutableListOf<CatModel>()
            val job = launch {
                catDetailsViewModel.readCatFlow.toList(catFlowResults)
            }
            assert(catDetailsViewModel.readStateFlow.value.isStart())
            catDetailsViewModel.updateFavoriteCatState(cat)
            assert(catDetailsViewModel.readStateFlow.value.isLoading())
            advanceUntilIdle()
            assert(catDetailsViewModel.readStateFlow.value.isSuccess())
            assert(catFlowResults.lastOrNull()?.favoriteId == "fId2")

            job.cancel()
        }
    }

    @Test
    fun `Remove Favorite Cat`() {
        runTest {
            val cat = CatModel(
                id = cat.id,
                favoriteId = "123"
            )
            val catFlowResults = mutableListOf<CatModel>()
            val job = launch {
                catDetailsViewModel.readCatFlow.toList(catFlowResults)
            }

            assert(catDetailsViewModel.readStateFlow.value.isStart())
            catDetailsViewModel.updateFavoriteCatState(cat)
            assert(catDetailsViewModel.readStateFlow.value.isLoading())
            advanceUntilIdle()
            assert(catDetailsViewModel.readStateFlow.value.isSuccess())
            assert(catFlowResults.firstOrNull()?.favoriteId.isNullOrBlank())

            job.cancel()
        }
    }
}