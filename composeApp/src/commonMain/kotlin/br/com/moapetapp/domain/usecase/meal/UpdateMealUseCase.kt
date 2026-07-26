package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal

class UpdateMealUseCase(
    private val repository: MealRepository,
) {
    suspend operator fun invoke(meal: Meal): Result<Meal> {
        validateMeal(meal)?.let { return Result.failure(it) }

        if (meal.id.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID do pacote de comida é obrigatório para atualizar")
            )
        }
        return repository.updateMeal(meal).map { meal }
    }
}