package br.com.moapetapp.di

import androidx.compose.animation.slideIn
import org.koin.dsl.module

val domainModule = module {
    // Use Cases vão aqui. Exemplo futuro:
    // single { AddPetUseCase(repository = get()) }
}

val dataModule = module {
    // repositórios vao aqui
}

val presentationModule = module {
    // viewlmodels vao aqui
}

val sharedModules = listOf(
    domainModule,
    dataModule,
    presentationModule
)