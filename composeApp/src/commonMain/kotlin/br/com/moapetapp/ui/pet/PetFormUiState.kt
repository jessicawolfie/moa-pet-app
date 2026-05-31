package br.com.moapetapp.ui.pet

import br.com.moapetapp.domain.model.Species

/**
 * Estado do formulário de criar/editar pet.
 *
 * @property name Nome digitado
 * @property species Espécie selecionada
 * @property breed Raça digitada
 * @property birthDateMillis Data de nascimento em epochDays (null = não selecionada)
 * @property weight Peso digitado (String para o input)
 * @property photoPath Caminho da foto (null = sem foto)
 * @property nameError Mensagem de erro do nome (null = sem erro)
 * @property weightError Mensagem de erro do peso (null = sem erro)
 * @property isSaving true enquanto salva (desabilita botão)
 * @property isSaved true quando salvo com sucesso (dispara navegação de volta)
 * @property isEditMode true se editando pet existente
 */

data class PetFormUiState(
    val name: String = "",
    val species: Species = Species.DOG,
    val breed: String = "",
    val birthDateEpochDays: Int? = null,
    val weight: String = "",
    val photoPath: String? = null,
    val nameError: String? = null,
    val weightError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false
)