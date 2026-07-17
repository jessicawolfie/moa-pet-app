package br.com.moapetapp.domain.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IsFeatureAvailableUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(feature: PremiumFeature): Flow<Boolean> {
        return subscriptionRepository.observeStatus().map { status ->
            when (status) {
                is SubscriptionStatus.Pro -> true
                is SubscriptionStatus.Cancelled -> true
                is SubscriptionStatus.Free -> false
                is SubscriptionStatus.Unknown -> false
            }
        }
    }
}