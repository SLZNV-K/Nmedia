package ru.netology.nmedia.error

sealed class AppError(var code: String) : RuntimeException(){
    companion object{
        fun from(e: Throwable): AppError = when (e){
            is ApiError -> e
            is NetworkError -> NetworkError
            else -> UnknownError
        }
    }
}
class ApiError(val status: Int, code: String) : AppError(code)
data object NetworkError : AppError("error_network")
data object UnknownError : AppError("error_unknown")