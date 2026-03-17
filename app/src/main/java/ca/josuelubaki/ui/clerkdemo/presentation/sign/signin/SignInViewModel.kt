package ca.josuelubaki.ui.clerkdemo.presentation.sign.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerk.api.Clerk
import com.clerk.api.network.serialization.errorMessage
import com.clerk.api.network.serialization.onFailure
import com.clerk.api.network.serialization.onSuccess
import com.clerk.api.network.serialization.successOrNull
import com.clerk.api.session.GetTokenOptions
import com.clerk.api.signin.SignIn
import com.clerk.api.signin.attemptSecondFactor
import com.clerk.api.signin.prepareSecondFactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            SignIn.create(SignIn.CreateParams.Strategy.Password(identifier = email, password = password))
                .onSuccess { signIn ->
                    Log.d("SignInViewModel", "SignIn success — status=${signIn.status}, sessionId=${signIn.createdSessionId}")
                    when (signIn.status) {
                        SignIn.Status.COMPLETE -> {
                            signIn.createdSessionId?.let { sessionId ->
                                Clerk.auth.setActive(sessionId = sessionId)
                                    .onSuccess {
                                        logClerkJwt()
                                        _uiState.value = SignInUiState.Success
                                    }
                                    .onFailure { Log.e("SignInViewModel", "setActive failed: ${it.errorMessage}", it.throwable) }
                            }
                        }
                        SignIn.Status.NEEDS_CLIENT_TRUST,
                        SignIn.Status.NEEDS_SECOND_FACTOR -> {
                            signIn.prepareSecondFactor()
                                .onSuccess {
                                    _uiState.value = SignInUiState.NeedsClientTrust
                                    logClerkJwt()
                                }
                                .onFailure { Log.e("SignInViewModel", "prepareSecondFactor failed: ${it.errorMessage}", it.throwable) }
                        }
                        else -> Log.w("SignInViewModel", "Unhandled status: ${signIn.status}")
                    }
                }
                .onFailure {
                    Log.e("SignInViewModel", "SignIn failed: ${it.errorMessage}", it.throwable)
                    _uiState.value = SignInUiState.Error
                }
        }
    }

    fun verifyClientTrust(code: String) {
        val inProgressSignIn = Clerk.client.signIn ?: return
        val strategy = inProgressSignIn.secondFactorVerification?.strategy
        val params = when (strategy) {
            SignIn.PrepareSecondFactorParams.PHONE_CODE -> SignIn.AttemptSecondFactorParams.PhoneCode(code)
            else -> SignIn.AttemptSecondFactorParams.EmailCode(code)
        }
        viewModelScope.launch {
            inProgressSignIn.attemptSecondFactor(params)
                .onSuccess { signIn ->
                    signIn.createdSessionId?.let { sessionId ->
                        Clerk.auth.setActive(sessionId = sessionId)
                            .onSuccess {
                                logClerkJwt()
                                _uiState.value = SignInUiState.Success
                            }
                            .onFailure { Log.e("SignInViewModel", "setActive failed: ${it.errorMessage}", it.throwable) }
                    }
                }
                .onFailure {
                    Log.e("SignInViewModel", "verifyClientTrust failed: ${it.errorMessage}", it.throwable)
                    _uiState.value = SignInUiState.Error
                }
        }
    }

    private suspend fun logClerkJwt() {
        val token = Clerk.auth.getToken(GetTokenOptions(template = "supabase")).successOrNull()
        if (token != null) {
            Log.d("SignInViewModel", "✅ JWT Clerk obtenu — longueur=${token.length}, début=${token.take(20)}...")
        } else {
            Log.w("SignInViewModel", "⚠️ JWT Clerk null — vérifie le JWT Template 'ClerkDemo' dans le dashboard Clerk")
        }
    }
}
