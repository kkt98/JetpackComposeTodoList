package com.example.todolistfromjetpackcompose

import androidx.compose.runtime.Composable

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
@Composable
fun NotificationPermissionRequest() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // State to track if we should show the permission dialog
    var shouldShowRequestPermissionDialog by remember { mutableStateOf(false) }

    // Register a permission launcher to request notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
    )

    // Check if permission is already granted
    val hasNotificationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission) {
            shouldShowRequestPermissionDialog = true
        }
    }

    if (shouldShowRequestPermissionDialog && activity != null) {
        AlertDialog(
            onDismissRequest = { shouldShowRequestPermissionDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    shouldShowRequestPermissionDialog = false
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = { shouldShowRequestPermissionDialog = false }) {
                    Text("Deny")
                }
            },
            title = { Text("Notification Permission") },
            text = { Text("This app needs notification permission to keep you updated.") }
        )
    }
}