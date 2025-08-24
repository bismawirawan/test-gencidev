package test.gencidev.common.states

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    class Error(val message: String) : UiState()
}