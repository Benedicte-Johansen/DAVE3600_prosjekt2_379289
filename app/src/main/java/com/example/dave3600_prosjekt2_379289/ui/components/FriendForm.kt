package com.example.dave3600_prosjekt2_379289.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dave3600_prosjekt2_379289.R

@Composable
fun FriendForm(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    errorMessage: String?,
    onDismissError: () -> Unit,
    isLoading: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    saveButtonText: String = "Lagre"
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feilmelding
        if (errorMessage != null) {
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
                        errorMessage,
                        modifier = Modifier.weight(1f),
                        color = Color.Red
                    )
                    IconButton(onClick = onDismissError) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = context.getString(R.string.close),
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Navn - Tillater alle tegn inkludert æøå
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = context.getString(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(context.getString(R.string.name_example)) },
            enabled = !isLoading
        )

        // Telefon - Kun tall tillatt
        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 8) {
                    onPhoneChange(newValue)
                }
            },
            label = { Text(text = context.getString(R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            placeholder = { Text(context.getString(R.string.phone_example)) },
            enabled = !isLoading
        )

        // Fødselsdag (format: DD.MM.YYYY)
        OutlinedTextField(
            value = birthDate,
            onValueChange = { newValue ->
                // Tillat kun tall og '.'
                if (newValue.all { it.isDigit() || it == '.' } && newValue.length <= 10) {
                    onBirthDateChange(newValue)
                }
            },
            label = { Text(text = context.getString(R.string.birthday_w_example)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = context.getString(R.string.birthday_example)) },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lagre-knapp
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(text = saveButtonText, color = Color.White, fontSize = 16.sp)
            }
        }

        // Avbryt-knapp
        Button(
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            enabled = !isLoading
        ) {
            Text(text = context.getString(R.string.abort), fontSize = 16.sp)
        }
    }
}