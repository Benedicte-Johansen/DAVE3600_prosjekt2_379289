package com.example.dave3600_prosjekt2_379289.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dave3600_prosjekt2_379289.R
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.ui.components.FriendForm
import com.example.dave3600_prosjekt2_379289.ui.friend.EditFriendViewModel

@Composable
fun EditFriendScreen(
    navController: NavController,
    repository: FriendRepository,
    friendId: Long
) {
    val viewModel: EditFriendViewModel = viewModel {
        EditFriendViewModel(repository)
    }

    val context = LocalContext.current
    // Samle UI-tilstand fra ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Lokale state-variabler for input-feltene
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }



    // Last inn vennen når skjermen åpnes
    LaunchedEffect(friendId) {
        viewModel.loadFriend(friendId)
    }

    // Fyll inn feltene når vennen er lastet
    LaunchedEffect(uiState.friend) {
        if (uiState.friend != null && !isInitialized) {
            name = uiState.friend!!.name
            phone = uiState.friend!!.phone
            birthDate = uiState.friend!!.birthDate
            isInitialized = true
        }
    }

    // Naviger tilbake når lagring er vellykket
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    // Vis loading-skjerm mens vennen lastes
    if (uiState.isLoadingFriend) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(context.getString(R.string.loading_friend))
            }
        }
        return
    }

    // Vis feilmelding hvis vennen ikke ble funnet
    if (uiState.friend == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    context.getString(R.string.friend_not_found),
                    fontSize = 18.sp,
                    color = Color.Red
                )
                Button(onClick = { navController.popBackStack() }) {
                    Text(context.getString(R.string.go_back))
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header med tilbake-knapp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = context.getString(R.string.back))
            }
            Text(context.getString(R.string.edit_friend), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        // Gjenbrukbart skjema
        FriendForm(
            name = name,
            onNameChange = { name = it },
            phone = phone,
            onPhoneChange = { phone = it },
            birthDate = birthDate,
            onBirthDateChange = { birthDate = it },
            errorMessage = uiState.errorMessage,
            onDismissError = { viewModel.clearError() },
            isLoading = uiState.isLoading,
            onSaveClick = {
                viewModel.updateFriend(friendId, name, phone, birthDate)
            },
            onCancelClick = { navController.popBackStack() },
            saveButtonText = context.getString(R.string.save)
        )
    }
}