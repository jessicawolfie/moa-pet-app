package br.com.moapetapp.presentation.pet

import br.com.moapetapp.domain.model.Pet

/**
 * Estado de tela de lista pets
 *
 * @property pets Lista de pets a exibir
 * @property isLoading true enquanto carrega dados
 * @property errorMessage mensagem de erro (null = sem erro)
 * @property isEmpty treu quando não há pets (estado vazio)
 */
data class PetListUiState(
   val pets: List<Pet> = emptyList(),
   val isLoading: Boolean = false,
   val errorMessage: String? = null
) {
    // Estado vazio: não está carregando E não tem pets E não tem erro
    val isEmpty: Boolean
        get() = !isLoading && pets.isEmpty() && errorMessage == null
}