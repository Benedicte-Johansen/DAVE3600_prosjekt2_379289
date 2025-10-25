package com.example.dave3600_prosjekt2_379289.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.ui.friend.AddFriendViewModel
import com.example.dave3600_prosjekt2_379289.R

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

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved){
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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = context.getString(R.string.back))
            }
            Text(text = context.getString(R.string.add_friend), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        if (uiState.errorMessage != null){
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
                        uiState.errorMessage ?: "",
                        modifier = Modifier.weight(1f),
                        color = Color.Red
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = context.getString(R.string.close),
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Navn
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = context.getString(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {Text(context.getString(R.string.name_example))},
            enabled = !uiState.isLoading
        )

        // Telefon
        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                if (newValue.all {it.isDigit()} && newValue.length <= 8){
                    phone = newValue
                }
            },
            label = { Text(text = context.getString(R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            placeholder = { Text(context.getString(R.string.phone_example))},
            enabled = !uiState.isLoading
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

        // Knapper
        Button(
            onClick = {
                viewModel.saveFriend(name, phone, birthDate)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(text = context.getString(R.string.save), color = Color.White, fontSize = 16.sp)
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            enabled = !uiState.isLoading
        ) {
            Text(text = context.getString(R.string.abort), fontSize = 16.sp)
        }
    }
}