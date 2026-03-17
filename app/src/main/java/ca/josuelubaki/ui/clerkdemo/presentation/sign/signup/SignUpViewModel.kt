package ca.josuelubaki.ui.clerkdemo.presentation.sign.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerk.api.Clerk
import com.clerk.api.network.serialization.errorMessage
import com.clerk.api.network.serialization.onFailure
import com.clerk.api.network.serialization.onSuccess
import com.clerk.api.signup.SignUp
import com.clerk.api.signup.attemptVerification
import com.clerk.api.signup.prepareVerification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val tag : String = SignUpViewModel::class.java.simpleName

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.SignedOut)
    val uiState = _uiState.asStateFlow()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            SignUp.create(SignUp.CreateParams.Standard(emailAddress = email, password = password))
                .onSuccess {
                    if (it.status == SignUp.Status.COMPLETE) {
                        _uiState.value = SignUpUiState.Success
                    } else {
                        _uiState.value = SignUpUiState.NeedsVerification
                        it.prepareVerification(SignUp.PrepareVerificationParams.Strategy.EmailCode())
                    }
                }
                .onFailure {
                    // See https://clerk.com/docs/guides/development/custom-flows/error-handling
                    // for more info on error handling
                    Log.e(tag, it.errorMessage, it.throwable)
                }
        }
    }

    fun verify(code: String) {
        val inProgressSignUp = Clerk.client.signUp ?: return
        viewModelScope.launch {
            inProgressSignUp.attemptVerification(SignUp.AttemptVerificationParams.EmailCode(code))
                .onSuccess { _uiState.value = SignUpUiState.Success }
                .onFailure {
                    // See https://clerk.com/docs/guides/development/custom-flows/error-handling
                    // for more info on error handling
                    Log.e("SignUpViewModel", it.errorMessage, it.throwable)
                }
        }
    }
}