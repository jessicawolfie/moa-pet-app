package br.com.moapetapp.domain.usecase.pet

import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.data.repository.PetRepository
import kotlinx.coroutines.flow.Flow

// Use Case para observar todos oe pets
// @property repository Repositório de pets
class GetAllPetsUseCase(
    private val repository: PetRepository
) {
    // Retorna Flow observável de todos os pets
    // Flow wemite nova lista sempre que há mudança no banco

    //@return Flow<List<Pet>> ordenado por nome
    operator fun invoke(): Flow<List<Pet>> {
        return repository.getAllPets()
    }
}