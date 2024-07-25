package pt.nunomatos.swordcats.data.model

sealed class ApiResponse<out T>(val data: T?) {
    data object Start : ApiResponse<Nothing>(data = null)

    data class Success<out T>(val responseData: T) : ApiResponse<T>(data = responseData)

    data object Loading : ApiResponse<Nothing>(data = null)

    sealed class Error : ApiResponse<Nothing>(data = null) {
        data object GenericError : Error()
        data object NetworkError : Error()
    }

    fun isSuccess(): Boolean {
        return this is Success
    }

    fun isStart(): Boolean {
        return this is Start
    }

    fun isLoading(): Boolean {
        return this is Loading
    }

    fun isError(): Boolean {
        return this is Error
    }

    fun isGenericError(): Boolean {
        return this is Error.GenericError
    }

    fun isNetworkError(): Boolean {
        return this is Error.NetworkError
    }
}