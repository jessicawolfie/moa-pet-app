package br.com.moapetapp.ui.vaccine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.Vaccine
import br.com.moapetapp.domain.usecase.vaccine.AddVaccineUseCase
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineByIdUseCase
import br.com.moapetapp.domain.usecase.vaccine.ScheduleVaccineReminderUseCase
import br.com.moapetapp.domain.usecase.vaccine.UpdateVaccineUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Estado do formulário de vacina
 * Datas são guardadas como epochDays (Int?) pra facilitar o bind com date picker
 */
data class VaccineFormUiState(
    val name: String = "",
    val appliedDateEpochDays: Int? = null,
    val nextDoseDateEpochDays: Int? = null,
    val reminderDaysBefore: String = "5",
    val veterinarian: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val dateError: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)

// Eventos da UI
sealed interface VaccineFormEvent {
    data class NameChanged(val value: String) : VaccineFormEvent
    data class AppliedDateChanged(val epochDays: Int?) : VaccineFormEvent
    data class NextDoseDateChanged(val epochDays: Int?) : VaccineFormEvent
    data class ReminderDaysBeforeChanged(val value: String) : VaccineFormEvent
    data class VeterinarianChanged(val value: String) : VaccineFormEvent
    data class NotesChanged(val value: String) : VaccineFormEvent
    data object Save : VaccineFormEvent
}

class VaccineFormViewModel(
    private val addVaccineUseCase: AddVaccineUseCase,
    private val updateVaccineUseCase: UpdateVaccineUseCase,
    private val getVaccineByIdUseCase: GetVaccineByIdUseCase,
    private val scheduleVaccineReminderUseCase: ScheduleVaccineReminderUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaccineFormUiState())
    val uiState: StateFlow<VaccineFormUiState> = _uiState.asStateFlow()

    private var petId: String = ""
    private var edtingVaccineId: String? = null

    // Guarda petId(sempre) e carrega a vacina se estiver editando
    fun initialize(petId: String, vaccineId: String?) {
        this.petId = petId
        if (vaccineId != null) {
            edtingVaccineId = vaccineId
            viewModelScope.launch {
                getVaccineByIdUseCase(vaccineId).onSuccess { vaccine ->
                    _uiState.value = _uiState.value.copy(
                        name = vaccine.name,
                        appliedDateEpochDays = vaccine.appliedDate.toEpochDays(),
                        nextDoseDateEpochDays = vaccine.nextDoseDate?.toEpochDays(),
                        reminderDaysBefore = vaccine.reminderDaysBefore.toString(),
                        veterinarian = vaccine.veterinarian ?: "",
                        notes = vaccine.notes ?: "",
                        isEditMode = true,
                    )
                }
            }
        }
    }

    fun onEvent(event: VaccineFormEvent) {
        when (event) {
            is VaccineFormEvent.NameChanged ->
                _uiState.value = _uiState.value.copy(name = event.value, nameError = null)

            is VaccineFormEvent.AppliedDateChanged ->
                _uiState.value =
                    _uiState.value.copy(appliedDateEpochDays = event.epochDays, dateError = null)

            is VaccineFormEvent.NextDoseDateChanged ->
                _uiState.value =
                    _uiState.value.copy(nextDoseDateEpochDays = event.epochDays, dateError = null)

            is VaccineFormEvent.ReminderDaysBeforeChanged ->
                _uiState.value = _uiState.value.copy(reminderDaysBefore = event.value)

            is VaccineFormEvent.VeterinarianChanged ->
                _uiState.value = _uiState.value.copy(veterinarian = event.value)

            is VaccineFormEvent.NotesChanged ->
                _uiState.value = _uiState.value.copy(notes = event.value)

            is VaccineFormEvent.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        var hasError = false

        // Validação: nome obrigatório
        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "Nome é obrigatório")
            hasError = true
        }

        // Validação: data de aplicação obrigatória
        if (state.appliedDateEpochDays == null) {
            _uiState.value = _uiState.value.copy(dateError = "Data de aplicação é obrigatória")
            hasError = true
        }
        if (hasError) return

        // Monta o Vaccine de domínio
        val vaccine = Vaccine(
            id = edtingVaccineId ?: "",
            petId = petId,
            name = state.name.trim(),
            appliedDate = LocalDate.fromEpochDays(state.appliedDateEpochDays!!),
            nextDoseDate = state.nextDoseDateEpochDays?.let { LocalDate.fromEpochDays(it) },
            reminderDaysBefore = state.reminderDaysBefore.toIntOrNull() ?: 5,
            veterinarian = state.veterinarian.trim().ifBlank { null },
            notes = state.notes.trim().ifBlank { null },
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            // 1) Persiste (add ou update)
            val result = if (state.isEditMode) {
                updateVaccineUseCase(vaccine).map { vaccine }
            } else {
                addVaccineUseCase(vaccine)
            }

            result.onSuccess { savedVaccine ->
                // 2) Agenda o lembrete da próxima dose
                scheduleVaccineReminderUseCase(savedVaccine)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    nameError = error.message,
                )
            }
        }
    }
}