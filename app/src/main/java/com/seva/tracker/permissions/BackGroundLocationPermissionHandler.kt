package com.seva.tracker.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
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
import android.provider.Settings
import kotlinx.coroutines.delay

//
//@Composable
//fun BackGroundLocationPermissionHandler(
//    onPermissionResult: (Boolean) -> Unit,
//) {
//    val context = LocalContext.current
//
//    var backgroundGranted by remember { mutableStateOf(false) }
//
//
//    // Лаунчер для запроса разрешения на фоновое местоположение
//    val backgroundRequestLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        backgroundGranted = isGranted
//        // Если версия ниже Android 10, фоновое разрешение не требуется, поэтому считаем его полученным
//        onPermissionResult(backgroundGranted)
//    }
//    LaunchedEffect(Unit) {
//        // Затем для Android 10+ проверяем разрешение на фоновое местоположение
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                backgroundGranted = true
//                onPermissionResult(backgroundGranted)
//            } else {
//                Log.d("vvv","backgroundRequestLauncher.launch")
//                backgroundRequestLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//            }
//        } else {
//            // Для версий ниже Android 10 фоновое разрешение не требуется
//            Log.d("vvv","ниже Android 10 фоновое разрешение не требуется")
//            backgroundGranted = true
//            onPermissionResult(backgroundGranted)
//        }
//    }
//}

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
        onPermissionResult(backgroundGranted)
    }

    // Лаунчер для открытия настроек геолокации
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Проверяем снова, включена ли геолокация
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Если геолокация включена
            backgroundGranted = true
            onPermissionResult(backgroundGranted)
        } else {
            // Если геолокация все еще не включена
            backgroundGranted = false
            onPermissionResult(backgroundGranted)
        }
    }

    LaunchedEffect(Unit) {
        // Для Android 10 и выше проверяем разрешение на фоновое местоположение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                backgroundGranted = true
                onPermissionResult(backgroundGranted)
            } else {
                Log.d("vvv", "backgroundRequestLauncher.launch")
                backgroundRequestLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        } else {
            // Для версий ниже Android 10 фоновое разрешение не требуется, но проверим геолокацию
            Log.d("vvv", "ниже Android 10 фоновое разрешение не требуется")

            // Проверим, включена ли геолокация
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d("vvv", "Геолокация отключена, открываем настройки")

                // Откроем настройки геолокации
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                locationSettingsLauncher.launch(intent)

            } else {
                Log.d("vvv", "Геолокация уже включена")
                backgroundGranted = true
                onPermissionResult(backgroundGranted)
            }
        }
    }
}



