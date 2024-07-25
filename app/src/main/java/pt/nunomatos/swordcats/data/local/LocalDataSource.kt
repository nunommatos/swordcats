package pt.nunomatos.swordcats.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) {
    companion object {
        private val KEY_CURRENT_USER_ID = stringPreferencesKey(
            name = "current_user_id"
        )
    }

    private val Context.dataStore by preferencesDataStore("${context.packageName}.preferences")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val loginStateFlow: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Unknown)
    private val currentUserFlow: MutableStateFlow<UserModel?> = MutableStateFlow(null)

    init {
        coroutineScope.launch {
            context.dataStore.data.map { it[KEY_CURRENT_USER_ID] }.collect { userId ->
                val user = userDao.getById(userId.orEmpty())
                loginStateFlow.emit(
                    if (user != null) {
                        LoginState.LoggedIn
                    } else {
                        LoginState.LoggedOut
                    }
                )
                currentUserFlow.emit(user)
            }
        }
    }

    fun listenToCurrentUser(): Flow<UserModel?> {
        return currentUserFlow.asStateFlow()
    }

    fun listenToLoginState(): Flow<LoginState> {
        return loginStateFlow.asStateFlow()
    }

    suspend fun getUserWithEmail(email: String): UserModel? {
        val user = userDao.getByEmail(email)
        if (user != null) {
            context.dataStore.edit {
                it[KEY_CURRENT_USER_ID] = user.id
            }
        }
        return user
    }

    suspend fun createUser(user: UserModel) {
        userDao.insert(user)
        context.dataStore.edit { it[KEY_CURRENT_USER_ID] = user.id }
    }

    suspend fun updateUser(user: UserModel) {
        currentUserFlow.emit(user)
        userDao.update(user)
    }

    suspend fun logout() {
        context.dataStore.edit { it[KEY_CURRENT_USER_ID] = "" }
    }
}