// composeApp/src/commonMain/kotlin/br/com/moapetapp/ui/Navigation.kt

package br.com.moapetapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.moapetapp.ui.meal.MealFormScreen
import br.com.moapetapp.ui.pet.PetDetailScreen
import br.com.moapetapp.ui.pet.PetListScreen
import br.com.moapetapp.ui.pet.PetFormScreen
import br.com.moapetapp.ui.vaccine.VaccineFormScreen
import br.com.moapetapp.ui.vaccine.VaccineListScreen
import br.com.moapetapp.ui.meal.MealScreen

/**
 * Configura o grafo de navegação do app.
 *
 * @param navController Controlador de navegação. Se não fornecido, cria um novo.
 * @param startDestination Tela inicial do app. Padrão: PetList.
 */
@Composable
fun MoaPetNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.PetList
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Tela: Lista de pets
        composable<Screen.PetList> {
            PetListScreen(
                onPetClick = { petId ->
                    navController.navigate(Screen.PetDetail(petId))
                },
                onAddPetClick = {
                    navController.navigate(Screen.PetForm())
                }
            )
        }

        // Tela: Formulário de pet (criar/editar)
        composable<Screen.PetForm> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.PetForm>()

            PetFormScreen(
                petId = args.petId,
                onNavigateBack = { navController.popBackStack() }
            )
        }


        // Tela: Detalhes de um pet
        composable<Screen.PetDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.PetDetail>()

            PetDetailScreen(
                petId = args.petId,
                onNavigateBack = { navController.popBackStack() },
                onVaccinesClick = { petId ->
                    navController.navigate(Screen.VaccineList(petId))
                },
                onMealsClick = { petId ->
                    navController.navigate(Screen.MealScreen(petId))
                },
                onEditClick = { petId ->
                    navController.navigate(Screen.PetForm(petId = petId))
                },
            )
        }

        composable<Screen.VaccineList> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.VaccineList>()
            VaccineListScreen(
                petId = args.petId,
                onNavigateBack = { navController.popBackStack() },
                onAddVaccineClick = { petId ->
                    navController.navigate(Screen.VaccineForm(petId = petId))
                },
                onVaccineClick = { petId, vaccineId ->
                    navController.navigate(Screen.VaccineForm(petId = petId, vaccineId = vaccineId))
                },
            )
        }

        composable<Screen.VaccineForm> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.VaccineForm>()
            VaccineFormScreen(
                petId = args.petId,
                vaccineId = args.vaccineId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<Screen.MealScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.MealScreen>()
            MealScreen(
                petId = args.petId,
                onNavigateBack = { navController.popBackStack() },
                onMealClick = { petId, mealId ->
                    navController.navigate(Screen.MealForm(petId = petId, mealId = mealId))
                }
            )
        }

        composable<Screen.MealForm> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.MealForm>()
            MealFormScreen(
                petId = args.petId,
                mealId = args.mealId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}