package br.com.moapetapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.moapetapp.domain.model.FoodType
import kotlinx.datetime.LocalDate

/**
 * Tabela "meals". Cada pacote de comida pertence a um pet (relação 1: N)
 *
 * @property foodType Tipo como String ("DRY", "RAW") — enum convertido no mapper
 *  @property totalAmount Quantidade total (gramas se DRY, pacotes se RAW)
 *  @property dailyAmount Consumo diário, mesma unidade
 *  @property reminderDaysBefore Antecedência do lembrete de reposição, em dias
 */
@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class MealEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val foodName: String,
    val foodType: String,
    val totalAmount: Double,
    val dailyAmount: Double,
    val purchaseDate: LocalDate,
    val reminderDaysBefore: Int,
    val notes: String?
)