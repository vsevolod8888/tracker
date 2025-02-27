package com.seva.tracker.permissions

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun BackGroundLocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    var backgroundGranted by remember { mutableStateOf(false) }


    // Лаунчер для запроса разрешения на фоновое местоположение
    val backgroundRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        backgroundGranted = isGranted
        // Если версия ниже Android 10, фоновое разрешение не требуется, поэтому считаем его полученным
        onPermissionResult(backgroundGranted)
    }
    LaunchedEffect(Unit) {

        // Затем для Android 10+ проверяем разрешение на фоновое местоположение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                backgroundGranted = true
                onPermissionResult(backgroundGranted)
            } else {
                backgroundRequestLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        } else {
            // Для версий ниже Android 10 фоновое разрешение не требуется
            backgroundGranted = true
            onPermissionResult(backgroundGranted)
        }
    }
}