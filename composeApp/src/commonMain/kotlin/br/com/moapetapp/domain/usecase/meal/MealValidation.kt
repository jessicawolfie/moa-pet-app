package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.domain.model.Meal

/**
 * Valida as regras de negócio comuns a add e update.
 * @return a exceção de validação, ou null se tudo válido.
 */

internal fun validateMeal(meal: Meal): Throwable? {
    // Nome obrigatório
    if (meal.foodName.isBlank()) {
        return IllegalArgumentException("Nome da comida é obrigatório.")
    }
    if (meal.foodName.trim().length < 2) {
        return IllegalArgumentException("Nome da comida deve ter pelo menos 2 caracteres.")
    }

    // Todo pacote pertence a um pet
    if (meal.petId.isBlank()) {
        return IllegalArgumentException("O pacote precisa estar associado a um pet.")
    }

    // Quantidade comprada deve ser positiva
    if (meal.totalAmount <= 0) {
        return IllegalArgumentException("Quantidade comprada deve ser maior que zero.")
    }

    // Consumo diário deve ser positivo
    if (meal.dailyAmount <= 0) {
        return IllegalArgumentException("Consumo diário deve ser maior que zero.")
    }


    // Consumo diário maior que o total = pacote não dura nem um dia
    if (meal.dailyAmount > meal.totalAmount) {
        return IllegalArgumentException("Consumo diário não pode ser maior que a quantidade total.")
    }

    // Antecedência do lembrete precisa ser não-negativa
    if (meal.reminderDaysBefore < 0) {
        return IllegalArgumentException("Antecedência do lembrete não pode ser negativa.")
    }

    return null
}