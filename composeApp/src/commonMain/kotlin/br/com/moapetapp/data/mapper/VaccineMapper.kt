package br.com.moapetapp.data.mapper

import br.com.moapetapp.data.local.entity.VaccineEntity
import br.com.moapetapp.domain.model.Vaccine

// Entity (banco) -> Domain
fun VaccineEntity.toDomain(): Vaccine = Vaccine(
    id = this.id,
    petId = this.petId,
    name = this.name,
    appliedDate = this.appliedDate,
    nextDoseDate = this.nextDoseDate,
    reminderDaysBefore = this.reminderDaysBefore,
    veterinarian = this.veterinarian,
    notes = this.notes
)

// Domain -> Entity (banco)
fun Vaccine.toEntity(): VaccineEntity = VaccineEntity(
    id = this.id,
    petId = this.petId,
    name = this.name,
    appliedDate = this.appliedDate,
    nextDoseDate = this.nextDoseDate,
    reminderDaysBefore = this.reminderDaysBefore,
    veterinarian = this.veterinarian,
    notes = this.notes
)

// Lista Entity -> Domain
fun List<VaccineEntity>.toDomain(): List<Vaccine> = this.map { it.toDomain() }
