package br.com.moapetapp.core.notification

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.contracts.contract

@Composable
actual fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
    ): NotificationPermissionRequester {
    // Launcher do contract de permissão: mostra o diálogo e devolve granted: Boolean
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onResult(granted)
    }

    return remember(launcher) {
        NotificationPermissionRequester {
            // POST_NOTIFICATIONS só existe no Android 13 (API 33)+
            // Abaixo disso, a permissão é automática -> considera concedida direto
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onResult(true)
            }
        }
    }

}