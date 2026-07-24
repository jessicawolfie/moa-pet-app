package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.data.repository.MealRepository

class DeleteMealUseCase(
    private val repository: MealRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID do pacote é obrigatório para deletar.")
            )
        }
        return repository.deleteMeal(id)
    }
}