package cc.apollai.location_service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cc.apollai.location_service.ui.ForegroundServiceScreen
import cc.apollai.location_service.ui.theme.KotlinLocationForegroundServiceTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var locationService: LocationForegroundService? = null

    private var serviceState by mutableStateOf(false)
    private var displayableLocation by mutableStateOf<String?>(null)

    companion object {
        private const val TAG = "MainActivity"
    }

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as LocationForegroundService.LocalBinder
            locationService = binder.getService()
            serviceState = true

            observeDataFlow()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            serviceState = false
            locationService = null
        }
    }

    private fun observeDataFlow() {
        Log.d(TAG, "observe location updates from the service")

        lifecycleScope.launch {
            lifecycleScope.launch {
                locationService?.locationData?.map {
                    it?.let { location ->
                        "${location.latitude}, ${location.longitude}"
                    }
                }?.collectLatest {
                    displayableLocation = it
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForegroundServiceScreen(
                serviceRunning = serviceState,
                currentLocation = displayableLocation,
                onClick = { clickStartOrStopForegroundService() })
        }

        checkAndRequestNotificationPermission()
        tryToBindToService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) {}

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startForegroundService()
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startForegroundService()
            }

            else -> {
                Toast.makeText(this, "Location permission is required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    // permission already granted
                }
                else -> {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun tryToBindToService() {
        Intent(this, LocationForegroundService::class.java).also { intent ->
            bindService(intent, connection, 0)
        }
    }

    private fun clickStartOrStopForegroundService() {
        if (locationService == null) {
            Log.d(TAG, "start foreground service")

            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            Log.d(TAG, "stop foreground service")
            locationService?.stopForegroundService()
        }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LocationForegroundService::class.java))
        }

        tryToBindToService()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinLocationForegroundServiceTheme {
        Greeting("Android")
    }
}