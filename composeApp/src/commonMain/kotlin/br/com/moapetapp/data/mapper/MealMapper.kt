package br.com.moapetapp.data.mapper

import br.com.moapetapp.data.local.entity.MealEntity
import br.com.moapetapp.domain.model.FoodType
import br.com.moapetapp.domain.model.Meal

fun MealEntity.toDomain(): Meal = Meal(
    id = this.id,
    petId = this.petId,
    foodName = this.foodName,
    // String do banco -> enum. Fallback pra DRY se vier valor inesperado
    foodType = FoodType.entries.firstOrNull { it.name == this.foodType } ?: FoodType.DRY,
    totalAmount = this.totalAmount.toInt(),
    dailyAmount = this.dailyAmount.toInt(),
    purchaseDate = this.purchaseDate,
    reminderDaysBefore = this.reminderDaysBefore,
    notes = this.notes,
)

fun Meal.toEntity(): MealEntity = MealEntity(
    id = this.id,
    petId = this.petId,
    foodName = this.foodName,
    foodType = this.foodType.name,   // enum -> String
    totalAmount = this.totalAmount.toDouble(),
    dailyAmount = this.dailyAmount.toDouble(),
    purchaseDate = this.purchaseDate,
    reminderDaysBefore = this.reminderDaysBefore,
    notes = this.notes,
)

fun List<MealEntity>.toDomain(): List<Meal> = map { it.toDomain() }