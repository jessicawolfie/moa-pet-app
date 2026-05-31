package br.com.moapetapp.ui.pet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.domain.model.Species
import br.com.moapetapp.ui.pet.PetFormEvent
import br.com.moapetapp.ui.pet.PetFormViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Tela de formulário de pet (criar/editar)
 *
 * @param petIt UUID do pet a editar (null = criar novo)
 * @param onNavigateBack Callback para voltar
 * @param viewModel ViewlModel inteado via koin
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: PetFormViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado do dropdown de espécie
    var speciesExpanded by remember { mutableStateOf(false) }

    // Navega de volta quando salvar com sucesso
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar Pet" else "Novo Pet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Botão salvar fixo no rodapé
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { viewModel.onEvent(PetFormEvent.Save)},
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Salvar Pet")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: nome
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(PetFormEvent.NameChanged(it)) },
                label = { Text("Nome *") },
                placeholder = { Text("Ex: Moa") },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: espécie (dropdown)
            ExposedDropdownMenuBox(
                expanded = speciesExpanded,
                onExpandedChange = { speciesExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.species.displayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Espécie *") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = speciesExpanded,
                    onDismissRequest = { speciesExpanded = false }
                ) {
                    Species.entries.forEach { species ->
                        DropdownMenuItem(
                            text = { Text(species.displayName()) },
                            onClick = {
                                viewModel.onEvent(PetFormEvent.SpeciesChanged(species))
                                speciesExpanded = false
                            }
                        )
                    }
                }
            }

            // Campo: raça
            OutlinedTextField(
                value = uiState.breed,
                onValueChange = { viewModel.onEvent(PetFormEvent.BreedChanged(it)) },
                label = { Text("Raça") },
                placeholder = { Text("Ex: Beagle") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: peso
            OutlinedTextField(
                value = uiState.weight,
                onValueChange = { viewModel.onEvent(PetFormEvent.WeightChanged(it)) },
                label = { Text("Peso(kg)") },
                placeholder = { Text("0.0") },
                isError = uiState.weightError != null,
                supportingText = uiState.weightError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Foto e data de nascimento serão adicionados em breve.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}