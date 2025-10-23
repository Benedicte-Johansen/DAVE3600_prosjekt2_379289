package com.example.dave3600_prosjekt2_379289.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.ui.friend.AddFriendViewModel

@Composable
fun AddFriendScreen(
    navController: NavController,
    repository: FriendRepository
) {
    val viewModel: AddFriendViewModel = viewModel {
        AddFriendViewModel(repository)
    }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Tilbake")
            }
            Text("Legg til venn", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        // Navn
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Navn") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Telefon
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefonnummer") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        // Fødselsdag (format: YYYY-MM-DD)
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Fødselsdag (DD-MM-YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("16-09-1999") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Knapper
        Button(
            onClick = {
                when {
                    name.isBlank() -> errorMessage = "Navn kan ikke være tomt"
                    phone.isBlank() -> errorMessage = "Telefon kan ikke være tomt"
                    birthDate.isBlank() -> errorMessage = "Fødselsdag kan ikke være tom"
                    !birthDate.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) ->
                        errorMessage = "Feil format på fødselsdag. Bruk DD-MM-YYYY"
                    else -> {
                        errorMessage = ""
                        viewModel.saveFriend(name, phone, birthDate)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Lagre", color = Color.White, fontSize = 16.sp)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Avbryt", fontSize = 16.sp)
        }
    }
}