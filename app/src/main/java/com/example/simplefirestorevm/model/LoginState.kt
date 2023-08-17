package com.example.simplefirestorevm.model

sealed class LoginState {
    object LoggedOut : LoginState()
    object Loading : LoginState()
    object LoggedIn : LoginState()
    object PwReset : LoginState()
    class  LoginError(val message: String? = null) : LoginState()
}
