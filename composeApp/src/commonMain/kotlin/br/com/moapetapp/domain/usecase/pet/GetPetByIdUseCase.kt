package br.com.moapetapp.domain.usecase.pet

import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.data.repository.PetRepository

// Use Case para buscar um pet específico por ID
class GetPetByIdUseCase(
    private val repository: PetRepository
) {
    // Busca pet por ID

    // @param petId UUID do pet
    // @return Result<Pet> com o pet encontrado ou erro
    suspend operator fun invoke(petId: String): Result<Pet> {
        // Valida que o ID não está vazio
        if (petId.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID do oet é obrigatório")
            )
        }

        return repository.getPetById(petId)
    }
}