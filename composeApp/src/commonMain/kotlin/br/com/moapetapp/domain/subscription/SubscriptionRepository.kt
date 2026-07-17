package br.com.moapetapp.domain.subscription

import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeStatus(): Flow<SubscriptionStatus>
}