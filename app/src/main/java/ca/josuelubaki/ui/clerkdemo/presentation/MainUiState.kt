package ca.josuelubaki.ui.clerkdemo.presentation

sealed interface MainUiState {
    data object Loading : MainUiState
    data object SignedIn : MainUiState
    data object SignedOut : MainUiState
}