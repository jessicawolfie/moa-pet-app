package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.data.repository.VaccineRepository

class DeleteVaccineUseCase(
    private val repository: VaccineRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID da vacina é obrigatório para deletar.")
            )
        }
        return repository.deleteVaccine(id)
    }
}