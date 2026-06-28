package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.domain.model.Vaccine

/**
 * Valida as regras de negócio comuns a add e update.
 * @return a exceção de validação, ou null se tudo válido.
 */
internal fun validateVaccine(vaccine: Vaccine): Throwable? {
    // Nome obrigatório, mínimo 2 caracteres
    if (vaccine.name.isBlank()) {
        return IllegalArgumentException("Nome da vacina é obrigatório.")
    }
    if (vaccine.name.trim().length < 2) {
        return IllegalArgumentException("Nome da vacina deve ter pelo menos 2 caracteres.")
    }

    // Toda vacina pertence a um pet
    if (vaccine.petId.isBlank()) {
        return IllegalArgumentException("A vacina precisa estar associada a um pet.")
    }

    // Se há próxima dose, ela não pode ser antes da aplicação
    vaccine.nextDoseDate?.let { next ->
        if (next < vaccine.appliedDate) {
            return IllegalArgumentException("A próxima dose não pode ser anterior à data de aplicação. ")
        }
    }

    return null
}
