package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal
import com.benasher44.uuid.uuid4

class AddMealUseCase(
    private val repository: MealRepository,
) {
    /**
     * Adiciona um pacote de comida com validações.
     * Gera UUID se o id vier vazio.
     * @return Result<Meal> com o pacote salvo (id preenchido) ou erro de validação.
     */
    suspend operator fun invoke(meal: Meal): Result<Meal> {
        validateMeal(meal)?.let { return Result.failure(it) }

        val mealWithId = if (meal.id.isBlank()) {
            meal.copy(id = uuid4().toString())
        } else {
            meal
        }
        return repository.addMeal(mealWithId)
            .map { mealWithId }
    }
}