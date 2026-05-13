package br.com.moapetapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: LocalDate?,
    val weightKg: Double?,
    val photoPath: String?
)
