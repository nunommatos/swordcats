package pt.nunomatos.swordcats.view_model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pt.nunomatos.swordcats.CoroutineTestRule
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.domain.use_case.GetCatsUseCase
import pt.nunomatos.swordcats.domain.use_case.GetLocalFeedUseCase
import pt.nunomatos.swordcats.domain.use_case.GetLoggedUserUseCase
import pt.nunomatos.swordcats.domain.use_case.LogoutUseCase
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import pt.nunomatos.swordcats.presentation.ui.cats.CatsViewModel
import pt.nunomatos.swordcats.repository.FakeCatsRepository
import pt.nunomatos.swordcats.repository.FakeUserRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CatsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule = CoroutineTestRule(testDispatcher)

    private lateinit var catsViewModel: CatsViewModel
    private val fakeCatsRepository = FakeCatsRepository()
    private val fakeUserRepository = FakeUserRepository()

    private val getCatsUseCase = GetCatsUseCase(fakeCatsRepository)
    private val getLocalFeedUseCase = GetLocalFeedUseCase(fakeCatsRepository)
    private val logoutUseCase = LogoutUseCase(fakeUserRepository)
    private val updateFavoriteCatStateUseCase = UpdateFavoriteCatStateUseCase(fakeCatsRepository)
    private val getLoggedUserUseCase = GetLoggedUserUseCase(fakeUserRepository)

    @Before
    fun setUp() {
        catsViewModel = CatsViewModel(
            getCatsUseCase = getCatsUseCase,
            getLocalFeedUseCase = getLocalFeedUseCase,
            getLoggedUserUseCase = getLoggedUserUseCase,
            logoutUseCase = logoutUseCase,
            updateFavoriteCatStateUseCase = updateFavoriteCatStateUseCase
        )
    }

    @Test
    fun `Get Cats`() {
        runTest {
            val states = mutableListOf<ApiResponse<*>>()
            val job = launch {
                catsViewModel.readMainStateFlow.toList(states)
            }

            catsViewModel.getCats()
            advanceUntilIdle()
            assert(states.size == 2)
            assert(states.first().isLoading())
            assert(states.last().isSuccess())
            assert(catsViewModel.readMainStateFlow.value.isSuccess())

            job.cancel()
        }
    }


    @Test
    fun `Get More Cats`() {
        runTest {
            val states = mutableListOf<ApiResponse<*>>()
            val job = launch {
                catsViewModel.readLoadMoreCatsState.toList(states)
            }

            catsViewModel.getMoreCats()
            advanceUntilIdle()
            assert(states.size == 2)
            assert(states.first().isLoading())
            assert(states.last().isError())
            assert(catsViewModel.readLoadMoreCatsState.value.isGenericError())

            job.cancel()
        }
    }

    @Test
    fun `Test Logout`() {
        runTest {
            val states = mutableListOf<Unit>()
            val job = launch {
                catsViewModel.readLogoutFlow.toList(states)
            }

            advanceUntilIdle()
            catsViewModel.logout()
            advanceUntilIdle()
            assert(states.isNotEmpty())

            job.cancel()
        }
    }

    @Test
    fun `Test Filter Favorites`() {
        runTest {
            val states = mutableListOf<Int>()
            val job = launch {
                catsViewModel.readCurrentSelectedTab.toList(states)
            }

            advanceUntilIdle()
            catsViewModel.updateSelectedTab(1, true)
            advanceUntilIdle()
            catsViewModel.updateSelectedTab(0, false)
            advanceUntilIdle()
            assert(states.size == 3)
            assert(states.first() == 0)
            assert(states[1] == 1)
            assert(states.last() == 0)

            job.cancel()
        }
    }
}