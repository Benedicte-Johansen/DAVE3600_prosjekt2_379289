package com.example.dave3600_prosjekt2_379289.ui.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI-tilstand for AddFriend-skjermen
data class AddFriendUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddFriendViewModel(
    private val repository: FriendRepository
) : ViewModel() {

    // Privat mutable state
    private val _uiState = MutableStateFlow(AddFriendUiState())
    // Public read-only state
    val uiState: StateFlow<AddFriendUiState> = _uiState.asStateFlow()

    /**
     * Lagrer en ny venn i databasen
     * @param name Vennens navn
     * @param phone Telefonnummer
     * @param birthDate Fødselsdato i format DD-MM-YYYY
     */
    fun saveFriend(name: String, phone: String, birthDate: String) {
        // Valider input
        val validationError = validateInput(name, phone, birthDate)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        // Start lagring
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val friend = Friend(
                    name = name.trim(),
                    phone = phone.trim(),
                    birthDate = birthDate.trim()
                )

                repository.insertFriend(friend)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Kunne ikke lagre venn: ${e.message}"
                )
            }
        }
    }

    /**
     * Validerer input-data
     * @return Feilmelding hvis validering feiler, null hvis alt er ok
     */
    private fun validateInput(name: String, phone: String, birthDate: String): String? {
        return when {
            name.isBlank() -> "Navn kan ikke være tomt"
            name.length < 2 -> "Navn må være minst 2 tegn"
            phone.isBlank() -> "Telefonnummer kan ikke være tomt"
            phone.length < 8 -> "Telefonnummer må være minst 8 siffer"
            birthDate.isBlank() -> "Fødselsdato kan ikke være tom"
            !isValidDateFormat(birthDate) -> "Ugyldig datoformat. Bruk DD-MM-YYYY (f.eks. 16-09-1999)"
            !isValidDate(birthDate) -> "Ugyldig dato. Sjekk dag, måned og år"
            else -> null
        }
    }

    /**
     * Sjekker om datoen har riktig format DD-MM-YYYY
     */
    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))
    }

    /**
     * Sjekker om datoen er gyldig (f.eks. ikke 32-13-2025)
     */
    private fun isValidDate(date: String): Boolean {
        if (!isValidDateFormat(date)) return false

        val parts = date.split("-")
        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false

        return when {
            month !in 1..12 -> false
            day !in 1..31 -> false
            year < 1900 || year > 2100 -> false
            month in listOf(4, 6, 9, 11) && day > 30 -> false // April, juni, sept, nov har 30 dager
            month == 2 && day > 29 -> false // Februar maks 29
            month == 2 && day == 29 && !isLeapYear(year) -> false // Skuddår-sjekk
            else -> true
        }
    }

    /**
     * Sjekker om året er et skuddår
     */
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Nullstiller feilmeldinger
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Nullstiller hele tilstanden (brukes når man forlater skjermen)
     */
    fun resetState() {
        _uiState.value = AddFriendUiState()
    }
}
