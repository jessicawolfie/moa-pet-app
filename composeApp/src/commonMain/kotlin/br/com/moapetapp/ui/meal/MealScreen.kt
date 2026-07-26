package br.com.moapetapp.ui.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.domain.model.Meal
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    petId: String,
    onNavigateBack: () -> Unit,
    onMealClick: (petId: String, mealId: String?) -> Unit,
    viewModel: MealViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.observeMeal(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Alimentação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onMealClick(petId, null) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar novo pacote")
            }
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            uiState.currentMeal == null -> {
                // Empty state: pet sem pacote cadastrado.
                Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Nenhum pacote cadastrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Toque no + para adicionar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                val current = uiState.currentMeal!!
                // Histórico sem o pacote atual (só os anteriores)
                val previous = uiState.history.filter { it.id != current.id }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        CurrentMealCard(
                            meal = current,
                            onClick = { onMealClick(petId, current.id) },
                        )
                    }
                    if (previous.isNotEmpty()) {
                        item {
                            Text(
                                "Histórico de pacotes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        items(items = previous, key = { it.id }) { meal ->
                            HistoryMealCard(
                                meal = meal,
                                onClick = { onMealClick(petId, meal.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

// Card do pacote atual: destaque com progresso e dias restantes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentMealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("PACOTE ATUAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(meal.foodName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Tipo de comida (ração/natural)
            Text(
                meal.foodType.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
            )

            // Barra de progresso de consumo
            val fraction = meal.consumedFraction
            if (fraction != null) {
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                )
            }

            // Dias restantes
            val days = meal.daysRemaining
            val daysText = when {
                days == null -> "Sem estimativa"
                days < 0 -> "Acabou há ${-days} dia (s)"
                days == 0 -> "Acaba hoje"
                else -> "$days dias restantes"
            }
            Text(
                daysText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if((days ?: 1) <= meal.reminderDaysBefore) {
                    MaterialTheme.colorScheme.tertiary // alerta quando perto de acabar
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )

            // COnsumo diário (com a unidade do tipo)
            Text(
                "Consumo ${meal.dailyAmount} ${meal.foodType.unitLabel}/dia",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryMealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column{
                Text(meal.foodName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${meal.foodType.displayName} - comprado em ${meal.purchaseDate} ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}