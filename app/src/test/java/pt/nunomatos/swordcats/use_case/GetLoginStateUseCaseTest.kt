package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.domain.use_case.GetLoginStateUseCase
import pt.nunomatos.swordcats.repository.FakeUserRepository

class GetLoginStateUseCaseTest {
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getLoginStateUseCase: GetLoginStateUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getLoginStateUseCase = GetLoginStateUseCase(fakeUserRepository)
    }

    @Test
    fun `Get Login State`() {
        runTest {
            val responses = mutableListOf<LoginState>()
            getLoginStateUseCase.invoke().toList(responses)
            assert(responses.size == 3)
            assert(responses.first().isUnknown())
            assert(responses[1].isLoggedIn())
            assert(responses.last().isLoggedOut())
        }
    }
}