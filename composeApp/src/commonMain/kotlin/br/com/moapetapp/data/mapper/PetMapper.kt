package br.com.moapetapp.data.mapper

import br.com.moapetapp.data.local.entity.PetEntity
import br.com.moapetapp.domain.model.Species
import br.com.moapetapp.domain.model.Pet

// Converte PetEntity (banco de dados) para Pet(domínio)
// @return Pet de domínio com os mesmos dados
fun PetEntity.toDomain(): Pet {
    return Pet(
        id = this.id,
        name = this.name,
        // Converte string do banco para enum species
        species = Species.fromString(this.species),
        breed = this.breed,
        birthDate = this.birthDate,
        weightKg = this.weightKg,
        photoFileName = this.photoFileName
    )
}

// Converte Pet(domínio) para PetEntity(banco de dados)
fun Pet.toEntity(): PetEntity {
    return PetEntity(
        id = this.id,
        name = this.name,
        // Converte enum species para string para o banco
        species = this.species.name,
        breed = this.breed,
        // LocalDate será convertido para Long pelo TypeCOnverter
        birthDate = this.birthDate,
        weightKg = this.weightKg,
        photoFileName = this.photoFileName
    )
}

// Converte lista de PetEntity para lista de pet
fun List<PetEntity>.toDomain(): List<Pet> {
    return this.map { it.toDomain() }
}
