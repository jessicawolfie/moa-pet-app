package br.com.moapetapp.presentation.pet

// Eventos que a uI pode disparar na tela de lista pets
sealed interface PetListEvent {

    // Recarrega a lista de pets manualmente (pull to refresh)
    data object LoadPets: PetListEvent

    // Deletar um pet específico
    // @property petId UUID do pet a deletar
    data class DeletePet(val petId: String) : PetListEvent

    //Limpar mensagem de erro (após exibir snackbar)
    data object ClearError : PetListEvent
}