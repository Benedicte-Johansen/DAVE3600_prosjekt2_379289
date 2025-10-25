package com.example.dave3600_prosjekt2_379289.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.dave3600_prosjekt2_379289.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsTestScreen(navController: NavController) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf(context.getString(R.string.test_message)) }
    var isSending by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.sms_test)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = context.getString(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                context.getString(R.string.test_func),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                context.getString(R.string.how_to_use_sms_test),
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
                label = { Text(context.getString(R.string.phone)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { context.getString(R.string.own_phone) },
                singleLine = true
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(context.getString(R.string.message)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Button(
                onClick = {
                    if (phoneNumber.isBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.phone_instruction),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (message.isBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.message_instruction),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // Sjekk SMS-tillatelse
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.SEND_SMS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_sms_permission),
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    isSending = true
                    try {
                        val smsManager = context.getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                        Toast.makeText(context, "SMS sendt til $phoneNumber!", Toast.LENGTH_LONG)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Feil ved sending: ${e.message}", Toast.LENGTH_LONG)
                            .show()
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
                    Text(context.getString(R.string.send_test_sms))
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
                        context.getString(R.string.testing_tips),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(context.getString(R.string.how_to_test))
                }
            }
        }
    }
}