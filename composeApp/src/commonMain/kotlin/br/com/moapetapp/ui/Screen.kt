package br.com.moapetapp.ui

import kotlinx.serialization.Serializable

/**
 * Representa todas as telas do app.
 * Cada objeto é uma rota navegável.
 */

sealed interface Screen {
    /**
     * Tela inicial - lista de pets do usuário
     * Rota: "pet_list"
     */
    @Serializable
    data object PetList : Screen

    /**
    * Tela de detalhes de um pet específico
     * Rota: "pet_detail/{petId}"
     *
     * @param petId ID do pet a exibir
    */
    @Serializable
    data class PetDetail(val petId: Long) : Screen

    /**
    * Tela de criaçao/ediçao de pet
     * Rota: "pet_form?petId={petId}"
     *
     * @param petId ID do pet a editar. Null = criar novo pet
    */
    @Serializable
    data class PetForm(val petId: Long? = null) : Screen

    /**
    * Tela de lista de vacinas de um pet
    * Rota: "vacine_list/{pet˜Id}"
    */
    @Serializable
    data class VaccineList(val petId: Long) : Screen
    /**
     * Tela de criação/edição de vacina.
     * Rota: "vaccine_form/{petId}?vaccineId={vaccineId}"
     */
    @Serializable
    data class VaccineForm(
        val petId: Long,
        val vaccineId: Long? = null
    ) : Screen

    /**
     * Tela de alimentação de um pet.
     * Rota: "meal/{petId}"
     */
    @Serializable
    data class MealScreen(val petId: Long) : Screen

    /**
     * Tela de medicações de um pet.
     * Rota: "medication_list/{petId}"
     */
    @Serializable
    data class MedicationList(val petId: Long) : Screen

    /**
     * Tela de calendário de observações.
     * Rota: "calendar/{petId}"
     */
    @Serializable
    data class CalendarScreen(val petId: Long) : Screen
}
