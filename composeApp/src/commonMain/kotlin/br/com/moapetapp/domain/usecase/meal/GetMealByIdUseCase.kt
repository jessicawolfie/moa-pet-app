package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal

class GetMealByIdUseCase(
    private val repository: MealRepository,
) {
    suspend operator fun invoke(id: String): Result<Meal> {
        return repository.getMealById(id)
    }
}