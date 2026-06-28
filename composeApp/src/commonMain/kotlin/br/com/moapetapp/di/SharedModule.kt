package br.com.moapetapp.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.moapetapp.core.image.ImageStorage
import br.com.moapetapp.core.image.provideImageStorage
import kotlinx.coroutines.Dispatchers
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import br.com.moapetapp.data.repository.PetRepositoryImpl
import br.com.moapetapp.data.repository.PetRepository
import br.com.moapetapp.domain.usecase.pet.*
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
}

val dataModule = module {
    single<MoaPetDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
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