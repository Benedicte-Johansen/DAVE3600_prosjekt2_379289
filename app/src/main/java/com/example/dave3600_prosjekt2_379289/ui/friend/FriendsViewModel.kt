package com.example.dave3600_prosjekt2_379289.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FriendsUiState(
    val friends: List<Friend> = emptyList(),
    val birthdayFriends: List<Friend> = emptyList()
)

class FriendsViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            repository.getAllFriendsStream().collect { friends ->
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                val birthdayFriends = friends.filter { friend ->
                    try {
                        val birthDate = LocalDate.parse(friend.birthDate, formatter)
                        birthDate.dayOfMonth == today.dayOfMonth &&
                                birthDate.monthValue == today.monthValue
                    } catch (e: Exception) {
                        false
                    }
                }

                _uiState.value = FriendsUiState(
                    friends = friends,
                    birthdayFriends = birthdayFriends
                )
            }
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch {
            repository.deleteFriend(friend)
        }
    }
}