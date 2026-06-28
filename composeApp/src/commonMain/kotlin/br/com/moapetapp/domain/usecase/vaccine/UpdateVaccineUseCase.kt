package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.domain.model.Vaccine

class UpdateVaccineUseCase(
    private val repository: VaccineRepository,
) {
    suspend operator fun invoke(vaccine: Vaccine): Result<Unit> {
        validateVaccine(vaccine)?.let { return Result.failure(it) }
        if (vaccine.id.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID da vacina é obrigatório para atualizar.")
            )
        }

        return repository.updateVaccine(vaccine)
    }
}