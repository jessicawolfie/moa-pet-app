package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(
    private val repository: MealRepository,
) {
    // Flow do histórico completo de pacotes do pet
    operator fun invoke(petId: String): Flow<List<Meal>> {
        return repository.getAllMealsForPet(petId)
    }
}