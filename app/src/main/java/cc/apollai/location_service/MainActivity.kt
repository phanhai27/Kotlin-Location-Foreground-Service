package cc.apollai.location_service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cc.apollai.location_service.ui.ForegroundServiceScreen
import cc.apollai.location_service.ui.theme.KotlinLocationForegroundServiceTheme

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
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            serviceState = false
            locationService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForegroundServiceScreen(
                serviceRunning = serviceState,
                currentLocation = displayableLocation,
                onClick = { /*TODO*/ })
        }
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