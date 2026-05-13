package br.com.moapetapp.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class Converters {
    // Converte LocalDate para Long (epochDays — dias desde 1970-01-01).
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDays()?.toLong()
    }

    // Converte Long para LocalDate.
    @TypeConverter
    fun toLocalDate(epochDays: Long?): LocalDate? {
        return epochDays?.let { LocalDate.fromEpochDays(it.toInt()) }
    }

    // Converte LocalTime para Int (segundos desde 00:00:00)
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }

    // Converte Int para LocalTime
    fun toLocalTime(secondOfDay: Int?): LocalTime? {
        return secondOfDay?.let { LocalTime.fromSecondOfDay(it) }
    }

    // Converte lista de horários para strng json
    @TypeConverter
    fun fromLocalTimeList(times: List<LocalTime>?): String? {
        return times?.joinToString (",") {it.toSecondOfDay().toString()}
    }

    // Converte string json para lista de horários
    @TypeConverter
    fun toLocalTimeList(data: String?): List<LocalTime>? {
        return data?.split(",")
            ?.map { it.toInt() }
            ?.map { LocalTime.fromSecondOfDay(it) }
    }
}
