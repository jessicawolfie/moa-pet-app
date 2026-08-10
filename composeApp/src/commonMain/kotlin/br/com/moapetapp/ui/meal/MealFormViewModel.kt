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
    val dateError: String? = null,
    val generalError: String? = null,
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
                _uiState.value = _uiState.value.copy(dailyAmount = event.value, amountError = null)
            is MealFormEvent.PurchaseDateChanged ->
                _uiState.value = _uiState.value.copy(purchaseDateEpochDays = event.epochDays, dateError = null)
            is MealFormEvent.ReminderDaysChanged ->
                _uiState.value = _uiState.value.copy(reminderDaysBefore = event.value)
            is MealFormEvent.NotesChanged ->
                _uiState.value = _uiState.value.copy(notes = event.value)
            MealFormEvent.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value

        val nameError = when {
            state.foodName.isBlank() -> "Nome é obrigatório"
            state.foodName.trim().length < 2 -> "Nome deve ter pelo menos 2 caracteres"
            else -> null
        }

        val total = state.totalAmount.replace(",", ".").toDoubleOrNull()?.toInt()
        val daily = state.dailyAmount.replace(",", ".").toDoubleOrNull()?.toInt()

        val amountError = when {
            total == null -> "Quantidade total é obrigatória"
            total <= 0 -> "Quantidade total deve ser maior que zero"
            daily == null -> "Consumo diário é obrigatório"
            daily <= 0 -> "Consumo diário deve ser maior que zero"
            daily > total -> "Consumo diário não pode ser maior que a quantidade total"
            else -> null
        }

        val dateError = if (state.purchaseDateEpochDays == null) "Informe a data de compra" else null

        // Aplica todos os erros de uma só vez
        _uiState.value = _uiState.value.copy(
            nameError = nameError,
            amountError = amountError,
            dateError = dateError,
            generalError = null,
        )

        if (nameError != null || amountError != null || dateError != null) {
            return
        }


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
                _uiState.value = _uiState.value.copy(isSavingMode = false, generalError = error.message)
            }
        }
    }
}
