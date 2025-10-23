package com.example.dave3600_prosjekt2_379289.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dave3600_prosjekt2_379289.data.AppDatabase
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BirthdayCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val smsEnabled = prefs.getBoolean("sms_enabled", false)

            if (!smsEnabled) {
                return Result.success()
            }

            val customMessage = prefs.getString("custom_message", "Gratulerer med fødselsdagen!")
                ?: "Gratulerer med fødselsdagen!"

            val database = AppDatabase.getDatabase(applicationContext)
            val repository = FriendRepository(database.friendDao())
            val friends = repository.getAllFriendsList()

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            friends.forEach { friend ->
                try {
                    val birthDate = LocalDate.parse(friend.birthDate, formatter)

                    // Sjekk om det er bursdagen (dag og måned)
                    if (birthDate.dayOfMonth == today.dayOfMonth &&
                        birthDate.monthValue == today.monthValue) {

                        // Sjekk SMS-tillatelse
                        if (ContextCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.SEND_SMS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            sendSms(friend.phone, customMessage)
                        }
                    }
                } catch (e: Exception) {
                    // Ignorer feil i datoformat for denne vennen
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = applicationContext.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            // Log feil hvis nødvendig
        }
    }
}