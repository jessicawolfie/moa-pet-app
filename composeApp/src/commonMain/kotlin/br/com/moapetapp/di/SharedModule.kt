package br.com.moapetapp.di

import androidx.room.RoomDatabase
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import br.com.moapetapp.data.repository.PetRepositoryImpl
import br.com.moapetapp.data.repository.PetRepository
import org.koin.dsl.module
import kotlin.math.sin

val domainModule = module {
    // Use Cases vão aqui. Exemplo futuro:
    // single { AddPetUseCase(repository = get()) }
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
    // viewlmodels vao aqui
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)