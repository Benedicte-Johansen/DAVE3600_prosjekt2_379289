package com.example.dave3600_prosjekt2_379289.ui.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI-tilstand for EditFriend-skjermen
data class EditFriendUiState(
    val friend: Friend? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val isLoadingFriend: Boolean = true
)

class EditFriendViewModel(
    private val repository: FriendRepository
) : ViewModel() {

    // Privat mutable state
    private val _uiState = MutableStateFlow(EditFriendUiState())
    // Public read-only state
    val uiState: StateFlow<EditFriendUiState> = _uiState.asStateFlow()

    //Laster inn en venn fra databasen
    fun loadFriend(friendId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingFriend = true)

                val friend = repository.getFriendById(friendId)

                if (friend != null) {
                    _uiState.value = _uiState.value.copy(
                        friend = friend,
                        isLoadingFriend = false,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingFriend = false,
                        errorMessage = "Fant ikke vennen"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingFriend = false,
                    errorMessage = "Kunne ikke laste venn: ${e.message}"
                )
            }
        }
    }

    fun updateFriend(id: Long, name: String, phone: String, birthDate: String) {
        // Valider input
        val validationError = InputValidation.validateInput(name, phone, birthDate)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        // Start oppdatering
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val friend = Friend(
                    id = id,
                    name = name.trim(),
                    phone = phone.trim(),
                    birthDate = birthDate.trim()
                )

                repository.updateFriend(friend)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Kunne ikke oppdatere venn: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
