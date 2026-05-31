package br.com.moapetapp.ui.pet

import br.com.moapetapp.domain.model.Species

// Eventos do formulário de pet
sealed interface PetFormEvent {

    // Nome alterado
    data class NameChanged(val name: String) : PetFormEvent

    // Espécie selecionada
    data class SpeciesChanged(val species: Species) : PetFormEvent

    // Raça alterada
    data class BreedChanged(val breed: String) : PetFormEvent

    // Data de nascimento selecionada (epochDays)
    data class BirthDateChanged(val epochDays: Int?) : PetFormEvent

    // Peso alterado
    data class WeightChanged(val weight: String) : PetFormEvent

    // Botão salvar pressionado
    data object Save: PetFormEvent
}