package ca.backyardbirds.core.network

sealed class NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(val exception: Throwable, val code: Int? = null) : NetworkResponse<Nothing>()
}
