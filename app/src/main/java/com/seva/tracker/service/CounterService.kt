package com.seva.tracker.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.seva.tracker.R
import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class CounterService : Service() {
    @Inject
    lateinit var settingsData: SettingsDataStore
    @Inject
    lateinit var repository: Repository


    private var counter = 0
    private var job: Job? = null
    private val channelId = "counter_service"
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            applicationContext
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification("Координаты")
        startForeground(1, notification)
        startCounting()
    }

    private fun startCounting() {
        job = CoroutineScope(Dispatchers.IO).launch {

            while (isActive) {
                delay(1000)
                counter++
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@launch
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        val lattt: Double? = location?.latitude
                        val loggg: Double? = location?.longitude
                        updateNotification("$lattt $loggg")
                        CoroutineScope(Dispatchers.IO).launch {
                            val routeId = settingsData.route_id.first()

                        if (lattt != null && loggg != null) {
                            Log.d("zzz", "saveCoord ${routeId}")
                                val newcoord = routeId.let {
                                    CoordinatesEntity(
                                        id = 0,
                                        checkTime = System.currentTimeMillis(),
                                        recordNumber = it,
                                        Lattitude = lattt,
                                        Longittude = loggg
                                    )
                                }
                            repository.insertCoord(newcoord)

                            }
                        }
                    }
            }
        }
    }

    private fun createNotification(countString: String): Notification {
        val stopIntent = Intent(this, CounterService::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Координаты")
            .setContentText(countString)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(android.R.drawable.ic_delete, "Остановить", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(count: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, createNotification(count))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Счётчик",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Канал для фонового счётчика"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == "STOP_SERVICE") {
            val stopIntent = Intent("STOP_SERVICE_ACTION")
            sendBroadcast(stopIntent) // Уведомляем UI
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        job?.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("zzz", "onDestroy() Service")
            val routeName = settingsData.routenameDataStore.first()
            val routeId = settingsData.route_id.first()
            delay(1000)
            Log.d("zzz", "settingsData.route_id.first() saved $routeId")
            var epochDays  = routeId/86400000
            val newRoute =
                RouteEntity(
                    id = routeId,
                    epochDays = epochDays.toInt(),
                    isDrawing = false,
                    checkTime = System.currentTimeMillis(),
                    recordRouteName = routeName,
                    isClicked = false
                )
            repository.insertRoute(newRoute)
            settingsData.saveRouteId(0L)
            Log.d("zzz", "onDestroy() End")
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
