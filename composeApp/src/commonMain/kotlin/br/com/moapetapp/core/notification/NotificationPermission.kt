package br.com.moapetapp.core.notification

import androidx.compose.runtime.Composable

/**
 * Cria um requester amarrado à plataforma
 * @param onResult callback com o resultado  (true = concedida/não-necessária)
 */
// Dispara o pedido de permissão de nitificação da plataforma
fun interface NotificationPermissionRequester {
    fun request()
}

@Composable
expect fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): NotificationPermissionRequester