package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.use_case.GetLoggedUserUseCase
import pt.nunomatos.swordcats.repository.FakeUserRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GetLoggedUserUseCaseTest {
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getLoggedUserUseCase: GetLoggedUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getLoggedUserUseCase = GetLoggedUserUseCase(fakeUserRepository)
    }

    @Test
    fun `Get Logged User`() {
        runTest {
            val responses = mutableListOf<UserModel?>()
            val job = launch {
                getLoggedUserUseCase.invoke().toList(responses)
            }

            advanceUntilIdle()
            assert(responses.size == 1)
            assert(responses.first() != null)

            job.cancel()
        }
    }
}