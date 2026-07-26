package br.com.moapetapp.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.FoodType
import br.com.moapetapp.domain.model.Meal
import br.com.moapetapp.domain.usecase.meal.AddMealUseCase
import br.com.moapetapp.domain.usecase.meal.GetMealByIdUseCase
import br.com.moapetapp.domain.usecase.meal.ScheduleFoodReminderUseCase
import br.com.moapetapp.domain.usecase.meal.UpdateMealUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Estado do formulário do pacote de comida.
 * Quantidades são String (input de texto); convertidas no save.
 */
data class MealFormUiState(
    val foodName: String = "",
    val foodType: FoodType = FoodType.DRY,
    val totalAmount: String = "",
    val dailyAmount: String = "",
    val purchaseDateEpochDays: Int? = null,
    val reminderDaysBefore: String = "5",
    val notes: String = "",
    val nameError: String? = null,
    val amountError: String? = null,
    val isEditMode: Boolean = false,
    val isSavingMode: Boolean = false,
    val isSaved: Boolean = false,
)

sealed interface MealFormEvent {
    data class FoodNameChanged(val value: String) : MealFormEvent
    data class FoodTypeChanged(val value: FoodType) : MealFormEvent
    data class TotalAmountChanged(val value: String) : MealFormEvent
    data class DailyAmountChanged(val value: String) : MealFormEvent
    data class PurchaseDateChanged(val epochDays: Int?) : MealFormEvent
    data class ReminderDaysChanged(val value: String) : MealFormEvent
    data class NotesChanged(val value: String) : MealFormEvent
    data object Save : MealFormEvent
}

class MealFormViewModel(
    private val addMealUseCase: AddMealUseCase,
    private val updateMealUseCase: UpdateMealUseCase,
    private val getMealByIdUseCase: GetMealByIdUseCase,
    private val scheduleFoodReminderUseCase: ScheduleFoodReminderUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealFormUiState())
    val uiState: StateFlow<MealFormUiState> = _uiState.asStateFlow()

    private var petId: String = ""
    private var editingMealId: String? = null

    fun initialize(petId: String, mealId: String?) {
        this.petId = petId
        if (mealId != null) {
            editingMealId = mealId
            viewModelScope.launch {
                getMealByIdUseCase(mealId).onSuccess { meal ->
                    _uiState.value = _uiState.value.copy(
                        foodName = meal.foodName,
                        foodType = meal.foodType,
                        totalAmount = meal.totalAmount.toString(),
                        dailyAmount = meal.dailyAmount.toString(),
                        purchaseDateEpochDays = meal.purchaseDate.toEpochDays(),
                        reminderDaysBefore = meal.reminderDaysBefore.toString(),
                        notes = meal.notes ?: "",
                        isEditMode = true,
                    )
                }
            }
        }
    }

    fun onEvent(event: MealFormEvent) {
        when (event) {
            is MealFormEvent.FoodNameChanged ->
                _uiState.value = _uiState.value.copy(foodName = event.value, nameError = null)
            is MealFormEvent.FoodTypeChanged ->
                _uiState.value = _uiState.value.copy(foodType = event.value)
            is MealFormEvent.TotalAmountChanged ->
                _uiState.value = _uiState.value.copy(totalAmount = event.value, amountError = null)
            is MealFormEvent.DailyAmountChanged ->
                _uiState.value = _uiState.value.copy(dailyAmount = event.value)
            is MealFormEvent.PurchaseDateChanged ->
                _uiState.value = _uiState.value.copy(purchaseDateEpochDays = event.epochDays)
            is MealFormEvent.ReminderDaysChanged ->
                _uiState.value = _uiState.value.copy(reminderDaysBefore = event.value)
            is MealFormEvent.NotesChanged ->
                _uiState.value = _uiState.value.copy(notes = event.value)
            MealFormEvent.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        var hasError = false

        if (state.foodName.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "Nome é obrigatório")
            hasError = true
        }

        // Converte as quantidades; se inválidas, erro
        // Usamos toIntOrNull para bater com o modelo Meal
        val total = state.totalAmount.replace(",", ".").toDoubleOrNull()?.toInt()
        val daily = state.dailyAmount.replace(",", ".").toDoubleOrNull()?.toInt()

        if (total == null || daily == null) {
            _uiState.value = _uiState.value.copy(amountError = "Informe quantidades válidas")
            hasError = true
        }

        if (state.purchaseDateEpochDays == null) {
            _uiState.value = _uiState.value.copy(amountError = "Informe data de compra")
            hasError = true
        }

        if (hasError) return

        val meal = Meal(
            id = editingMealId ?: "",
            petId = petId,
            foodName = state.foodName.trim(),
            foodType = state.foodType,
            totalAmount = total!!,
            dailyAmount = daily!!,
            purchaseDate = LocalDate.fromEpochDays(state.purchaseDateEpochDays!!),
            reminderDaysBefore = state.reminderDaysBefore.toIntOrNull() ?: 5,
            notes = state.notes.trim().ifBlank { null },
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingMode = true)

            val result = if (state.isEditMode) {
                updateMealUseCase(meal)
            } else {
                addMealUseCase(meal)
            }

            result.onSuccess { savedMeal ->
                scheduleFoodReminderUseCase(savedMeal)
                _uiState.value = _uiState.value.copy(isSavingMode = false, isSaved = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSavingMode = false, nameError = error.message)
            }
        }
    }
}
