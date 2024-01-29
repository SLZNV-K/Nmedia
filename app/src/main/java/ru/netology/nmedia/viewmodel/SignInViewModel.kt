package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModelState
import java.io.IOException

class SignInViewModel : ViewModel() {
    private val _authStateModel = MutableLiveData(AuthModelState())
    val authStateModel: LiveData<AuthModelState>
        get() = _authStateModel

    fun updateUser(login: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = PostApi.service.updateUser(login, pass)

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                AppAuth.getInstance().setAuth(body.id, body.token!!)
            } catch (e: IOException) {
                _authStateModel.value = AuthModelState(error = true)
            } catch (e: Exception) {
                _authStateModel.value = AuthModelState(error = true)
            }
        }
    }
}