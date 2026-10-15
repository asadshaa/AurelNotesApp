package com.aurel.notes.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Manages biometric and device credential authentication for Aurel App Lock & Private Vault.
 */
class BiometricPromptManager(private val context: Context) {

    sealed class AuthStatus {
        data object Ready : AuthStatus()
        data object NotAvailable : AuthStatus()
        data object NoneEnrolled : AuthStatus()
        data object Unsupported : AuthStatus()
    }

    sealed class AuthResult {
        data object Success : AuthResult()
        data class Error(val code: Int, val message: String) : AuthResult()
        data object Failed : AuthResult()
    }

    fun checkAuthStatus(): AuthStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> AuthStatus.Ready
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> AuthStatus.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> AuthStatus.NotAvailable
            else -> AuthStatus.Unsupported
        }
    }

    fun prompt(
        activity: FragmentActivity,
        title: String = "Unlock Aurel Vault",
        subtitle: String = "Authenticate to access protected notes",
        negativeButtonText: String = "Cancel",
        onResult: (AuthResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(AuthResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onResult(AuthResult.Error(errorCode, errString.toString()))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onResult(AuthResult.Failed)
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        val prompt = BiometricPrompt(activity, executor, callback)
        prompt.authenticate(promptInfo)
    }
}
