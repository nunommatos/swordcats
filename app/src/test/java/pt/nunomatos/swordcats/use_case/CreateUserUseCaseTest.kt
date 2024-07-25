package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.use_case.CreateUserUseCase
import pt.nunomatos.swordcats.repository.FakeUserRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserUseCaseTest {
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var createUserUseCase: CreateUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        createUserUseCase = CreateUserUseCase(fakeUserRepository)
    }

    @Test
    fun `Test Create User`() {
        runTest {
            val responses = mutableListOf<UserModel?>()
            val job = launch {
                fakeUserRepository.listenToCurrentUser().toList(responses)
            }
            advanceUntilIdle()
            createUserUseCase.invoke("name", "email")
            advanceUntilIdle()
            assert(responses.size == 2)
            val newUser = responses.last()
            assert(newUser?.name == "name")
            assert(newUser?.email == "email")

            job.cancel()
        }
    }
}