package br.com.moapetapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        // Aplica configuraçoes específicas da plataforma
        appDeclaration()

        // Carrega todos os módulos compartilhados
        modules(sharedModules)
    }
}