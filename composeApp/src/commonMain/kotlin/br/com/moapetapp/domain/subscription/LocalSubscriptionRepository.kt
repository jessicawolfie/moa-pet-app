package br.com.moapetapp.domain.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Implementação TEMPORÁRIA para o MVP do PRO
class LocalSubscriptionRepository(
    private val initialStatus: SubscriptionStatus = SubscriptionStatus.Free
) : SubscriptionRepository {

    private val statusFlow = MutableStateFlow(initialStatus)

    override fun observeStatus(): Flow<SubscriptionStatus> = statusFlow.asStateFlow()

    // Método auxiliar só pra debug/teste manual, simulando upgrade.
    fun debugSetStatus(status: SubscriptionStatus) {
        statusFlow.value = status
    }
}