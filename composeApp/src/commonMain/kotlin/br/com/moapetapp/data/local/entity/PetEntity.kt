package br.com.moapetapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

/**
 * Tabela "pets" no banco de dados SQLite.
 *
 * @property id UUID do pet (gerado pelo app, não autoincrement)
 * @property name Nome do pet (obrigatório)
 * @property species Espécie como String ("DOG", "CAT", etc)
 * @property breed Raça (opcional)
 * @property birthDate Data de nascimento como Long (epochDays via TypeConverter)
 * @property weightKg Peso em kg (opcional)
 * @property photoFileName Nome do arquivo da foto no armazenamento local
 */

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: LocalDate?,
    val weightKg: Double?,
    val photoFileName: String?,
)
