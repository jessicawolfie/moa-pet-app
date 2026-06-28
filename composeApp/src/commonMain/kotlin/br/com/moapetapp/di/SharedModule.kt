package br.com.moapetapp.di

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import br.com.moapetapp.core.image.ImageStorage
import br.com.moapetapp.core.image.provideImageStorage
import kotlinx.coroutines.Dispatchers
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import br.com.moapetapp.data.repository.PetRepositoryImpl
import br.com.moapetapp.data.repository.PetRepository
import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.data.repository.VaccineRepositoryImpl
import br.com.moapetapp.domain.usecase.pet.*
import br.com.moapetapp.domain.usecase.vaccine.AddVaccineUseCase
import br.com.moapetapp.domain.usecase.vaccine.DeleteVaccineUseCase
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineByIdUseCase
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineForPetUseCase
import br.com.moapetapp.domain.usecase.vaccine.UpdateVaccineUseCase
import org.koin.dsl.module
import br.com.moapetapp.presentation.pet.PetListViewModel
import org.koin.core.module.dsl.viewModelOf
import br.com.moapetapp.ui.pet.PetFormViewModel
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

}

val presentationModule = module {
    viewModelOf(::PetListViewModel)
    viewModelOf(::PetFormViewModel)
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)