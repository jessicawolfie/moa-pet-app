package br.com.moapetapp.data.local

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
actual object MoaPetDatabaseConstructor : RoomDatabaseConstructor<MoaPetDatabase> {
    override fun initialize(): MoaPetDatabase = initialize()
}
