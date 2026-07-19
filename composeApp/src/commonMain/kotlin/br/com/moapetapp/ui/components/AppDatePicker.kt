package br.com.moapetapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDate

private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

/**
 * Campo de data reutilizável: um OutlinedTextField read-only que abre um DatePickerDialog ao tocar
 * Trabalha em epochDays, convertendo de/para millis internamente
 *
 * @param label rótulo do campo
 * @paramepochDays valor atual
 * @param onDateSelected callback com o novo epochDays (ou null se limpar)
 * @param isError destaca o campo em erro
 * @param supportingText texto de apoio/erro
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateField(
    label: String,
    epochDays: Int?,
    onDateSelected: (Int?) -> Unit,
    isError: Boolean = false,
    supportingText: String? = null,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }

    // epochDays -> texto legível (ou vazio)
    val text = epochDays?.let { LocalDate.fromEpochDays(it).toString() } ?: ""

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            enabled = true, // Mantemos true mas tratamos o clique acima
            isError = isError,
            supportingText = supportingText?.let { { Text(it) } },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
        )
        // Camada transparente clicável por cima
        Surface(
            color = Color.Transparent,
            onClick = { showPicker = true },
            modifier = Modifier.matchParentSize(),
        ) {}
    }

    if (showPicker) {
        val initialMillis = epochDays?.let { it.toLong() * MILLIS_PER_DAY }
        val dateState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newEpochDays =
                        dateState.selectedDateMillis?.let { (it / MILLIS_PER_DAY).toInt() }
                    onDateSelected(newEpochDays)
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = dateState)
        }
    }
}


