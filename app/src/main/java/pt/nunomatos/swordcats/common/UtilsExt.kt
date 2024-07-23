package pt.nunomatos.swordcats.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.NoNetworkException
import retrofit2.Response

fun Context.hasInternetConnection(): Boolean {
    val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? (ConnectivityManager)
    val networkInfo = connectionManager?.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}

fun Context.openUrl(url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }
        startActivity(intent)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

fun <T> (suspend () -> Response<T>).toApiResponseFlow(): Flow<ApiResponseModel<T>> {
    val call = this
    return flow {
        emit(ApiResponseModel.Loading)
        val callResponse = call()
        try {
            val responseBody = callResponse.body()
            if (callResponse.isSuccessful && responseBody != null) {
                emit(ApiResponseModel.Success(responseBody))
            } else {
                emit(ApiResponseModel.Error.GenericError)
            }
        } catch (e: Exception) {
            emit(ApiResponseModel.Error.GenericError)
        }
    }.catch { exception ->
        emit(
            if (exception is NoNetworkException) {
                ApiResponseModel.Error.NetworkError
            } else {
                ApiResponseModel.Error.GenericError
            }
        )
    }
}

fun <T> (suspend () -> Response<T>).toNullableApiResponseFlow(): Flow<ApiResponseModel<T?>> {
    val call = this
    return flow {
        emit(ApiResponseModel.Loading)
        val callResponse = call()
        try {
            val responseBody = callResponse.body()
            if (callResponse.isSuccessful) {
                emit(ApiResponseModel.Success(responseBody))
            } else {
                emit(ApiResponseModel.Error.GenericError)
            }
        } catch (e: Exception) {
            emit(ApiResponseModel.Error.GenericError)
        }
    }.catch { exception ->
        emit(
            if (exception is NoNetworkException) {
                ApiResponseModel.Error.NetworkError
            } else {
                ApiResponseModel.Error.GenericError
            }
        )
    }
}