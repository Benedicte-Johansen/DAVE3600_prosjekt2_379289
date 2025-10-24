package com.example.dave3600_prosjekt2_379289

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.example.dave3600_prosjekt2_379289.data.AppDatabase
import com.example.dave3600_prosjekt2_379289.data.FriendRepository
import com.example.dave3600_prosjekt2_379289.ui.PreferencesViewModel
import com.example.dave3600_prosjekt2_379289.ui.screens.*
import com.example.dave3600_prosjekt2_379289.ui.theme.DAVE3600_prosjekt2_379289Theme
import com.example.dave3600_prosjekt2_379289.workers.BirthdayCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var repository: FriendRepository
    private lateinit var preferencesViewModel: PreferencesViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("sms_enabled", isGranted).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        repository = FriendRepository(database.friendDao())
        preferencesViewModel = PreferencesViewModel(applicationContext)

        setupDailyBirthdayCheck()

        setContent {
            DAVE3600_prosjekt2_379289Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "friends") {
            composable("friends") {
                FriendsScreen(navController, repository)
            }
            composable("add_friend") {
                AddFriendScreen(navController, repository)
            }
            composable(
                route = "edit_friend/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.LongType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getLong("friendId") ?: 0L
                EditFriendScreen(navController, repository, friendId)
            }
            composable("preferences") {
                PreferencesScreen(navController, preferencesViewModel, requestPermissionLauncher)
            }
            composable("sms_test") {
                SmsTestScreen(navController)
            }
        }
    }

    private fun setupDailyBirthdayCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<BirthdayCheckWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "BirthdayCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)

            if (timeInMillis <= currentTime) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }
        return calendar.timeInMillis - currentTime
    }
}