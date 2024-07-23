package pt.nunomatos.swordcats.data.model

sealed class ApiResponseModel<out T>(val data: T?) {
    data object Start : ApiResponseModel<Nothing>(data = null)

    data class Success<out T>(val responseData: T) : ApiResponseModel<T>(data = responseData)

    data object Loading : ApiResponseModel<Nothing>(data = null)

    sealed class Error : ApiResponseModel<Nothing>(data = null) {
        data object GenericError : Error()
        data object NetworkError : Error()
    }

    fun isSuccess(): Boolean {
        return this is Success
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