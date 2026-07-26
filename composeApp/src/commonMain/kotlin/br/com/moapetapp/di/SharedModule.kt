package br.com.moapetapp.di

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import br.com.moapetapp.core.image.ImageStorage
import br.com.moapetapp.core.image.provideImageStorage
import br.com.moapetapp.core.notification.NotificationScheduler
import br.com.moapetapp.core.notification.provideNotificationScheduler
import kotlinx.coroutines.Dispatchers
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.data.repository.MealRepositoryImpl
import br.com.moapetapp.data.repository.PetRepositoryImpl
import br.com.moapetapp.data.repository.PetRepository
import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.data.repository.VaccineRepositoryImpl
import br.com.moapetapp.domain.usecase.meal.AddMealUseCase
import br.com.moapetapp.domain.usecase.meal.DeleteMealUseCase
import br.com.moapetapp.domain.usecase.meal.GetCurrentMealUseCase
import br.com.moapetapp.domain.usecase.meal.GetHistoryUseCase
import br.com.moapetapp.domain.usecase.meal.GetMealByIdUseCase
import br.com.moapetapp.domain.usecase.meal.ScheduleFoodReminderUseCase
import br.com.moapetapp.domain.usecase.meal.UpdateMealUseCase
import br.com.moapetapp.domain.usecase.pet.*
import br.com.moapetapp.domain.usecase.vaccine.AddVaccineUseCase
import br.com.moapetapp.domain.usecase.vaccine.DeleteVaccineUseCase
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineByIdUseCase
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineForPetUseCase
import br.com.moapetapp.domain.usecase.vaccine.ScheduleVaccineReminderUseCase
import br.com.moapetapp.domain.usecase.vaccine.UpdateVaccineUseCase
import br.com.moapetapp.ui.meal.MealViewModel
import br.com.moapetapp.ui.meal.MealFormViewModel
import org.koin.dsl.module
import br.com.moapetapp.ui.pet.PetListViewModel
import br.com.moapetapp.ui.pet.PetDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import br.com.moapetapp.ui.pet.PetFormViewModel
import br.com.moapetapp.ui.vaccine.VaccineFormViewModel
import br.com.moapetapp.ui.vaccine.VaccineListViewModel
import kotlinx.coroutines.IO


val domainModule = module {
    // Use Cases - regra de negócio
    factory { AddPetUseCase(repository = get()) }
    factory { UpdatePetUseCase(repository = get()) }
    factory { DeletePetUseCase(repository = get()) }
    factory { GetAllPetsUseCase(repository = get()) }
    factory { GetPetByIdUseCase(repository = get()) }

    // Vacina
    factory { AddVaccineUseCase(repository = get()) }
    factory { UpdateVaccineUseCase(repository = get()) }
    factory { DeleteVaccineUseCase(repository = get()) }
    factory { GetVaccineForPetUseCase(repository = get()) }
    factory { GetVaccineByIdUseCase(repository = get()) }
    factory { ScheduleVaccineReminderUseCase(notificationScheduler = get()) }

    // Alimentação
    factory { AddMealUseCase(repository = get()) }
    factory { UpdateMealUseCase(repository = get()) }
    factory { DeleteMealUseCase(repository = get()) }
    factory { GetCurrentMealUseCase(repository = get()) }
    factory { GetHistoryUseCase(repository = get()) }
    factory { GetMealByIdUseCase(repository = get()) }
    factory { ScheduleFoodReminderUseCase(notificationScheduler = get()) }
}

val dataModule = module {
    single<MoaPetDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(connection: SQLiteConnection) {
                    connection.execSQL("PRAGMA foreign_keys = ON")
                }
            } )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    // DAOs
    single { get<MoaPetDatabase>().petDao() }
    single { get<MoaPetDatabase>().vaccineDao() }
    single { get<MoaPetDatabase>().mealDao() }

    // Repositories
    // PetRepository registrado pela interface (não pela classe)
    // Isso permite trocar a implementação em testes sem alterar quem consome
    single<PetRepository> {
        PetRepositoryImpl(petDao = get())
    }

    // Vacinas
    single<VaccineRepository> {
        VaccineRepositoryImpl(vaccineDao = get())
    }

    // Armazenamento de imagens
    single<ImageStorage> { provideImageStorage() }

    // Notificações
    single<NotificationScheduler> { provideNotificationScheduler() }

    // Alimentação
    single<MealRepository> { MealRepositoryImpl(mealDao = get()) }

}

val presentationModule = module {
    viewModelOf(::PetListViewModel)
    viewModelOf(::PetFormViewModel)
    viewModelOf(::PetDetailViewModel)
    viewModelOf(::VaccineListViewModel)
    viewModelOf(::VaccineFormViewModel)
    viewModelOf(::MealViewModel)
    viewModelOf(::MealFormViewModel)
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)