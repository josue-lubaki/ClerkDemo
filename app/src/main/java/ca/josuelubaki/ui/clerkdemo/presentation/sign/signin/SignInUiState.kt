package ca.josuelubaki.ui.clerkdemo.presentation.sign.signin

sealed interface SignInUiState {
    data object Idle : SignInUiState

    data object Error : SignInUiState

    data object Success : SignInUiState

    data object NeedsClientTrust : SignInUiState
}
