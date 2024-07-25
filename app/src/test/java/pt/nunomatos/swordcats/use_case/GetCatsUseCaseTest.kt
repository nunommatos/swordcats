package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.domain.use_case.GetCatsUseCase
import pt.nunomatos.swordcats.repository.FakeCatsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GetCatsUseCaseTest {

    private lateinit var getCatsUseCase: GetCatsUseCase
    private lateinit var fakeCatsRepository: FakeCatsRepository

    @Before
    fun setUp() {
        fakeCatsRepository = FakeCatsRepository()
        getCatsUseCase = GetCatsUseCase(fakeCatsRepository)
    }

    @Test
    fun `Get Cats List`() {
        runTest {
            val responses = mutableListOf<ApiResponse<*>>()
            val job = launch {
                getCatsUseCase.invoke(initialRequest = true).toList(responses)
            }
            advanceUntilIdle()
            assert(responses.size == 3)
            assert(responses.first().isLoading())
            assert(responses[1].isLoading())
            assert(responses.last().isSuccess())
            job.cancel()
        }
    }

    @Test
    fun `Get Cats List Page 1`() {
        runTest {
            val responses = mutableListOf<ApiResponse<*>>()
            val job = launch {
                getCatsUseCase.invoke(false).toList(responses)
            }
            advanceUntilIdle()
            assert(responses.size == 2)
            assert(responses.first().isLoading())
            assert(responses.last().isGenericError())

            job.cancel()
        }
    }
}