package br.com.moapetapp.di

import androidx.room.RoomDatabase
import br.com.moapetapp.data.local.MoaPetDatabase
import br.com.moapetapp.data.local.getDatabaseBuilder
import org.koin.dsl.module

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
    // Futuramente aqui virão:
    // single { get<MoaPetDatabase>().petDao() }
    // single<PetRepository> { PetRepositoryImpl(dao = get()) }
}

val presentationModule = module {
    // viewlmodels vao aqui
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)