package br.com.moapetapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform.getKoin

actual fun getDatabaseBuilder(): RoomDatabase.Builder<MoaPetDatabase> {
    val context = getKoin().get<Context>()
    val dbFile = context.getDatabasePath(MoaPetDatabase.DATABASE_NAME)
    return Room.databaseBuilder<MoaPetDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
}
