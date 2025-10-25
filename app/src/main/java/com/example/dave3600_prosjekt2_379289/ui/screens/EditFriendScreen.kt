package com.example.dave3600_prosjekt2_379289.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dave3600_prosjekt2_379289.R
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
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

    // Lokale state-variabler for input-feltene
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    // Samle UI-tilstand fra ViewModel
    val uiState by viewModel.uiState.collectAsState()

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
            Text("Rediger venn", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        // Feilmelding (hvis det er noen)
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFC62828)
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Lukk",
                            tint = Color(0xFFC62828)
                        )
                    }
                }
            }
        }

        // Navn-felt
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(context.getString(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            placeholder = { Text(context.getString(R.string.name_example)) }
        )

        // Telefon-felt
        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                if (newValue.all{it.isDigit()} && newValue.length <= 8){
                    phone = newValue
                }
            },
            label = { Text(context.getString(R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !uiState.isLoading,
            placeholder = { Text(context.getString(R.string.phone_example)) }
        )

        // Fødselsdag (format: DD.MM.YYYY)
        OutlinedTextField(
            value = birthDate,
            onValueChange = { newValue ->
                //Tillater kun tall og "."
                if (newValue.all {it.isDigit() || it == '.'} && newValue.length <= 10){
                    birthDate = newValue
                }
            },
            label = { Text(text = context.getString(R.string.birthday_w_example)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = context.getString(R.string.birthday_example)) },
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lagre-knapp
        Button(
            onClick = {
                viewModel.updateFriend(friendId, name, phone, birthDate)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(context.getString(R.string.save_changes), color = Color.White, fontSize = 16.sp)
            }
        }

        // Avbryt-knapp
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            enabled = !uiState.isLoading
        ) {
            Text(context.getString(R.string.abort), fontSize = 16.sp)
        }
    }
}