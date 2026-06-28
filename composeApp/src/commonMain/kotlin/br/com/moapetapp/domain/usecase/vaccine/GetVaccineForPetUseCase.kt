package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow

class GetVaccineForPetUseCase(
    private val repository: VaccineRepository,
) {
    // Flow observável das vacinas de um pet (orientada pela query do DAO)
    operator fun invoke(petId: String): Flow<List<Vaccine>> {
        return repository.getVaccinesForPet(petId)
    }
}