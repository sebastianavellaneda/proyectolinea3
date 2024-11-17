package com.example.futbolteamup.utils


object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordStrong(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() }
    }
}
