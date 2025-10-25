package com.example.dave3600_prosjekt2_379289.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dave3600_prosjekt2_379289.R
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.ui.components.FriendForm
import com.example.dave3600_prosjekt2_379289.ui.friend.AddFriendViewModel

@Composable
fun AddFriendScreen(
    navController: NavController,
    repository: FriendRepository
) {
    val viewModel: AddFriendViewModel = viewModel {
        AddFriendViewModel(repository)
    }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    // Naviger tilbake når lagring er vellykket
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = context.getString(R.string.back)
                )
            }
            Text(
                text = context.getString(R.string.add_friend),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
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
                viewModel.saveFriend(name, phone, birthDate)
            },
            onCancelClick = { navController.popBackStack() },
            saveButtonText = context.getString(R.string.save)
        )
    }
}