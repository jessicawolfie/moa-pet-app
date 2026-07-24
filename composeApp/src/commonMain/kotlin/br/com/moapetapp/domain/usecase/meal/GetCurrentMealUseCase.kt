package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal
import kotlinx.coroutines.flow.Flow

class GetCurrentMealUseCase(
    private val repository: MealRepository,
) {
    // Flow do pacote atual do pet (null se não houver)
    operator fun invoke(petId: String): Flow<Meal?> {
        return repository.getCurrentMealForPet(petId)
    }
}