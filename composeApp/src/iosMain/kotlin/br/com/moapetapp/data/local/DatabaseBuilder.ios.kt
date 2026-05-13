package br.com.moapetapp.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<MoaPetDatabase> {
    val dbFilePath = NSHomeDirectory() + "/${MoaPetDatabase.DATABASE_NAME}"

    return Room.databaseBuilder<MoaPetDatabase>(
        name = dbFilePath
    )
}
