package br.com.moapetapp.core.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): NotificationPermissionRequester {
    return remember {
        NotificationPermissionRequester {onResult(true) }
    }
}