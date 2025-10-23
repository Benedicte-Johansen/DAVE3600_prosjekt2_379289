package com.example.dave3600_prosjekt2_379289.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesViewModel(context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _smsEnabled = MutableStateFlow(prefs.getBoolean("sms_enabled", false))
    val smsEnabled: StateFlow<Boolean> = _smsEnabled.asStateFlow()

    private val _customMessage = MutableStateFlow(prefs.getString("custom_message", "Gratulerer med fødselsdagen!") ?: "Gratulerer med fødselsdagen!")
    val customMessage: StateFlow<String> = _customMessage.asStateFlow()

    fun setSmsEnabled(enabled: Boolean) {
        _smsEnabled.value = enabled
        prefs.edit().putBoolean("sms_enabled", enabled).apply()
    }

    fun setCustomMessage(message: String) {
        _customMessage.value = message
        prefs.edit().putString("custom_message", message).apply()
    }
}