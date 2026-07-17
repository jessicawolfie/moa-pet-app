package br.com.moapetapp.domain.subscription

import kotlinx.datetime.Instant

sealed class SubscriptionStatus {
    // Usuário nunca assinou, ou cancelou e o período pago já terminou
    data object Free : SubscriptionStatus()

    // Assinatura ativa. Guarda a data de expiração para permitir avisar "sua assinatura renova em 3 dias" na UI
    data class Pro(val expiresAt: Instant) : SubscriptionStatus()

    // Estado transitório: usuário cancelou, mas ainda está dentro do período pago
    data class Cancelled(val accessUntil: Instant) : SubscriptionStatus()

    // Validação com a loja falhou ou está indisponível
    data object Unknown : SubscriptionStatus()
}