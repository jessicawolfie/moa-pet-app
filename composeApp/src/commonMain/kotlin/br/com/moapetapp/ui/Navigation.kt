// composeApp/src/commonMain/kotlin/br/com/moapetapp/ui/Navigation.kt

package br.com.moapetapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.moapetapp.ui.pet.PetDetailScreen
import br.com.moapetapp.ui.pet.PetListScreen
import br.com.moapetapp.ui.pet.PetFormScreen

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
                onVaccinesClick = {}
            )
        }
    }
}