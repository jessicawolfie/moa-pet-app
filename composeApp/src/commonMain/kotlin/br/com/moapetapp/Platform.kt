package br.com.moapetapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform