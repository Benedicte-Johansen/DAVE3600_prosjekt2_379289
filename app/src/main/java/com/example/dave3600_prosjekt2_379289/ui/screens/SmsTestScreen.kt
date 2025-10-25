package com.example.dave3600_prosjekt2_379289.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsTestScreen(navController: NavController) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Test melding fra Bursdagsappen!") }
    var isSending by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMS Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tilbake")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Test SMS-funksjonalitet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Bruk denne skjermen for å teste om appen kan sende SMS-meldinger.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                    }
                },
                label = { Text("Telefonnummer") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ditt eget telefonnummer") },
                singleLine = true
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Melding") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Button(
                onClick = {
                    if (phoneNumber.isBlank()) {
                        Toast.makeText(context, "Vennligst skriv inn et telefonnummer", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (message.isBlank()) {
                        Toast.makeText(context, "Vennligst skriv inn en melding", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Sjekk SMS-tillatelse
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.SEND_SMS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(context, "SMS-tillatelse er ikke gitt", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    isSending = true
                    try {
                        val smsManager = context.getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                        Toast.makeText(context, "SMS sendt til $phoneNumber!", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Feil ved sending: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isSending = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send test-SMS")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tips for testing:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Skriv inn ditt eget telefonnummer")
                    Text("2. Trykk på 'Send test-SMS'")
                    Text("3. Sjekk om du mottar meldingen")
                    Text("4. Hvis du ikke mottar melding, sjekk SMS-tillatelser i innstillinger")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "For å teste bursdagsfunksjonalitet:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Legg til en venn med dagens dato som bursdag")
                    Text("2. Bruk formatet: DD.MM.YYYY (f.eks. ${getCurrentDate()})")
                    Text("3. Vent til klokken 09:00 neste dag, eller test WorkManager manuelt")
                }
            }
        }
    }
}

private fun getCurrentDate(): String {
    val calendar = java.util.Calendar.getInstance()
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val month = (calendar.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    val year = calendar.get(java.util.Calendar.YEAR)
    return "$day-$month-$year"
}