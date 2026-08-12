package br.com.moapetapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

/**
 * Tabela "vaccines". Cada vacina pertence a um pet (relação 1:N).
 *
 * @property id UUID da vacina
 * @property petId FK para pets.id - cascade: se apagar o pet, apaga as suas vacinas
 * @property name Nome da vacina
 * @property appliedDate Data de aplicação (epochDays via TypeConverter)
 * @property nextDoseDate Data da próxima dose - opcional
 * @property veterinarian Veterinário responsável - opcional
 * @property notes Observaçvões - opcional
 */
@Entity(
    tableName = "vaccines",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("petId")],
)
data class VaccineEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val name: String,
    val appliedDate: LocalDate,
    val nextDoseDate: LocalDate?,
    val reminderDaysBefore: Int,
    val veterinarian: String?,
    val notes: String?
)