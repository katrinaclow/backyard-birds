package ca.backyardbirds.domain.model

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Failure(val message: String, val cause: Throwable? = null) : DomainResult<Nothing>()
}
