package br.com.moapetapp.ui.vaccine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.domain.model.Vaccine
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineListScreen(
    petId: String,
    onNavigateBack: () -> Unit,
    onAddVaccineClick: (petId: String) -> Unit,
    onVaccineClick: (petId: String, vaccineId: String) -> Unit,
    viewModel: VaccineListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observa as vacinas do pet ao abrir
    LaunchedEffect(petId) {
        viewModel.observeVaccines(petId)
    }

    // Aba selecionada: 0 = próximas, 1 = aplicadas
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacinas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddVaccineClick(petId) }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Vacina")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Abas próximas / Aplicadas
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Próximas") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Aplicadas") }
                )
            }

            // Conteúdo da aba selecionada
            val vaccines = if (selectedTab == 0) uiState.upcoming else uiState.applied

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                vaccines.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (selectedTab == 0) "Nenhuma dose futura agendada."
                            else "Nenhuma dose de vacina aplicada registrada.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items = vaccines, key = { it.id }) { vaccine ->
                            VaccineCard(
                                vaccine = vaccine,
                                onClick = { onVaccineClick(petId, vaccine.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Card de uma vacina. Toque abre o formulário de edição
@Composable
private fun VaccineCard(vaccine: Vaccine, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(vaccine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusBadge(vaccine)
            }
            vaccine.veterinarian?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "Aplicada em ${vaccine.appliedDate}",
                style = MaterialTheme.typography.bodySmall,
            )
            vaccine.nextDoseDate?.let { next ->
                Text(
                    "Próxima dose: $next",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

// Badge de status baseado no daysUntilNextDose (computed property do domínio).
@Composable
private fun StatusBadge(vaccine: Vaccine) {
    val days = vaccine.daysUntilNextDose
    val (label, color) = when {
        days == null -> "Aplicada" to MaterialTheme.colorScheme.secondaryContainer
        days < 0 -> "Atrasada" to MaterialTheme.colorScheme.errorContainer
        days <= 7 -> "Próxima dose" to MaterialTheme.colorScheme.tertiaryContainer
        else -> "Em dia" to MaterialTheme.colorScheme.primaryContainer
    }
    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}