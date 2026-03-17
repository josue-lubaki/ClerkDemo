package ca.josuelubaki.ui.clerkdemo.presentation.sign.signup

sealed interface SignUpUiState {
    data object SignedOut : SignUpUiState
    data object Success : SignUpUiState
    data object NeedsVerification : SignUpUiState
}
