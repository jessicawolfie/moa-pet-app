package br.com.moapetapp.di

import androidx.room.RoomDatabase
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import br.com.moapetapp.data.repository.PetRepositoryImpl
import br.com.moapetapp.data.repository.PetRepository
import br.com.moapetapp.domain.usecase.pet.*
import org.koin.dsl.module
import kotlin.math.sin
 import br.com.moapetapp.presentation.pet.PetListViewModel
import org.koin.core.module.dsl.viewModelOf

val domainModule = module {
    // Use Cases - regra de negócio
    factory { AddPetUseCase(repository = get()) }
    factory { UpdatePetUseCase(repository = get()) }
    factory { DeletePetUseCase(repository = get()) }
    factory { GetAllPetsUseCase(repository = get()) }
    factory { GetPetByIdUseCase(repository = get()) }
}

val dataModule = module {
    single<MoaPetDatabase> {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(dropAllTables = true)  // Por enquanto, em dev
            .build()
    }
    // DAOs
    single { get<MoaPetDatabase>().petDao() }

    // Repositories
    // PetRepository registrado pela interface (não pela classe)
    // Isso permite trocar a implementação em testes sem alterar quem consome
    single<PetRepository> {
        PetRepositoryImpl(petDao = get())
    }
}

val presentationModule = module {
    viewModelOf(::PetListViewModel)
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)