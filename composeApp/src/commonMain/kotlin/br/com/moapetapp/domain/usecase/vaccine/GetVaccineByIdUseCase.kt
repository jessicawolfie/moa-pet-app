package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.domain.model.Vaccine

class GetVaccineByIdUseCase(
    private val repository: VaccineRepository,
) {
    suspend operator fun invoke(id: String): Result<Vaccine> {
        return repository.getVaccineById(id)
    }
}