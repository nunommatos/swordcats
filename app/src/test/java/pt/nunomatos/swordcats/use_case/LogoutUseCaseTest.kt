package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.use_case.LogoutUseCase
import pt.nunomatos.swordcats.repository.FakeUserRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutUseCaseTest {
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        logoutUseCase = LogoutUseCase(fakeUserRepository)
    }

    @Test
    fun `Test Logout`() {
        runTest {
            val responses = mutableListOf<UserModel?>()
            val job = launch {
                fakeUserRepository.listenToCurrentUser().toList(responses)
            }
            logoutUseCase.invoke()
            advanceUntilIdle()
            assert(responses.size == 2)
            assert(responses.first() != null)
            assert(responses.last() == null)

            job.cancel()
        }
    }
}