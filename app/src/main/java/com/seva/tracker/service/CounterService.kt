package com.seva.tracker.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.seva.tracker.R
import com.seva.tracker.data.datastore.SettingsDataStore
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.room.CoordinatesEntity
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.presentation.mapDraw.calculateRouteLength
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

    private val channelId = "counter_service"
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private var lastSavedTime = 0L
    private var minTimeMillis = 10 * 60 * SECOND.toInt()

    private var locationRequest = createLocationRequest(Priority.PRIORITY_BALANCED_POWER_ACCURACY)

    private var coordinatesList = mutableListOf<LatLng>()
    private var routeLength = ""
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            adjustLocationRequest(location.speed)
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastSavedTime > minTimeMillis) {
                lastSavedTime = currentTime
                saveLocation(location.latitude, location.longitude)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            val routeId = settingsData.routeId.first()
            repository.coordtListLiveFlow(routeId).collect { coordinates ->
                coordinatesList =
                    coordinates.map { LatLng(it.lattitude, it.longittude) }.toMutableList()
                routeLength = calculateRouteLength(
                    coordinatesList,
                    resources.getString(R.string.km),
                    resources.getString(R.string.meters)
                )
            }
        }
        createNotificationChannel()
        val notification = createNotification(resources.getString(R.string.waitingcoords))
        startForeground(1, notification)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun adjustLocationRequest(speed: Float) {
        val newPriority = when {
            speed > 5 -> Priority.PRIORITY_HIGH_ACCURACY
            speed > 0.5 -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            else -> Priority.PRIORITY_LOW_POWER
        }

        if (locationRequest.priority != newPriority) {
            locationRequest = createLocationRequest(newPriority)
            minTimeMillis = when (newPriority) {
                Priority.PRIORITY_HIGH_ACCURACY -> 20 * SECOND.toInt()
                Priority.PRIORITY_BALANCED_POWER_ACCURACY -> 60 * SECOND.toInt()
                else -> 10 * 60 * SECOND.toInt()
            }
            startLocationUpdates()
        }
    }

    private fun createLocationRequest(priority: Int): LocationRequest {
        return LocationRequest.Builder(priority, LOCATION_REQUEST_INTERVAL)
            .setMinUpdateIntervalMillis(MIN_REQUEST_INTERVAL)
            .setMinUpdateDistanceMeters(10f)
            .build()
    }

    private fun saveLocation(lat: Double, lon: Double) {
        val newCoord = LatLng(lat, lon)
        coordinatesList.add(newCoord)


        updateNotification("$lat $lon")
        serviceScope.launch {
            routeLength = calculateRouteLength(
                coordinatesList,
                resources.getString(R.string.km),
                resources.getString(R.string.meters)
            )

            val routeId = settingsData.routeId.first()
            val newCoord = CoordinatesEntity(
                id = 0,
                checkTime = System.currentTimeMillis(),
                recordNumber = routeId,
                lattitude = lat,
                longittude = lon
            )
            repository.insertCoord(newCoord)
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
            .setContentTitle(resources.getString(R.string.coordinates))
            .setContentText(countString)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(
                android.R.drawable.ic_delete,
                resources.getString(R.string.ostanovit),
                stopPendingIntent
            )
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
            "Counter",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Background counter channel"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            val stopIntent = Intent("STOP_SERVICE_ACTION")
            sendBroadcast(stopIntent)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        val job = serviceScope.launch {
            routeLength = calculateRouteLength(
                coordinatesList,
                resources.getString(R.string.km),
                resources.getString(R.string.meters)
            )
            val routeName = settingsData.routenameDataStore.first()
            val routeId = settingsData.routeId.first()
            delay(SECOND)
            val epochDays = routeId / MILLISECONDS_IN_DAY
            val newRoute = RouteEntity(
                id = routeId,
                lenght = routeLength,
                epochDays = epochDays.toInt(),
                isDrawing = false,
                checkTime = System.currentTimeMillis(),
                recordRouteName = routeName,
                isClicked = false
            )
            repository.insertRoute(newRoute)
            settingsData.saveRouteId(0L)
        }
        runBlocking { job.join() }
        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val SECOND = 1000L
        const val LOCATION_REQUEST_INTERVAL = 5000L
        const val MIN_REQUEST_INTERVAL = 5000L
        const val MILLISECONDS_IN_DAY = 86400000
    }
}