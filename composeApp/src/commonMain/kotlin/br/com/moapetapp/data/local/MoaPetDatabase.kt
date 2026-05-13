package br.com.moapetapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.moapetapp.data.local.dao.PetDao
import br.com.moapetapp.data.local.entity.PetEntity

@Database(
    entities = [
        PetEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MoaPetDatabase : RoomDatabase() {
    
    abstract fun petDao(): PetDao
    
    companion object {
        const val DATABASE_NAME = "moapetapp.db"
    }
}
