package cc.apollai.location_service.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.apollai.location_service.R
import cc.apollai.location_service.ui.theme.KotlinLocationForegroundServiceTheme

@Composable
internal fun ForegroundServiceScreen(
    serviceRunning: Boolean,
    currentLocation: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KotlinLocationForegroundServiceTheme {
        Surface(
            modifier =Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ForegroundServiceScreenContent(
                serviceRunning = serviceRunning,
                currentLocation = currentLocation,
                onClick = onClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ForegroundServiceScreenContent(
    serviceRunning: Boolean,
    currentLocation: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.status_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (serviceRunning) {
                stringResource(id = R.string.status_running)
            } else {
                stringResource(id = R.string.status_not_running)
            },
            color = if (serviceRunning) {
                Color.Green
            } else {
                Color.Red
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onClick) {
            Text(
                text = stringResource(
                    id = if (serviceRunning) {
                        R.string.button_stop
                    } else {
                        R.string.button_start
                    }
                )
            )
        }

    }
}