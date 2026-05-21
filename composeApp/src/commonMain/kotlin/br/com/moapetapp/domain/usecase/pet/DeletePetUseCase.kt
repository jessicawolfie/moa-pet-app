package br.com.moapetapp.domain.usecase.pet

import br.com.moapetapp.data.repository.PetRepository

// Use case para deletar um pet
// @property repository Repositório de pets
class DeletePetUseCase(
    private val repository: PetRepository
) {
    // Deleta um pet por ID
    // @param petId UUID do pet a deletar
    // @return Result<Unit> indicando sucesso ou erro
    suspend operator fun invoke(petId: String): Result<Unit> {
        // Valida que o ID não está vazio
        if (petId.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID do pet é obrigatório.")
            )
        }

        return repository.deletePet(petId)
    }
}