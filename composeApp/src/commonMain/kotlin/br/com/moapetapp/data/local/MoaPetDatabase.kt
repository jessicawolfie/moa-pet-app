package br.com.moapetapp.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import br.com.moapetapp.data.local.dao.PetDao
import br.com.moapetapp.data.local.dao.VaccineDao
import br.com.moapetapp.data.local.entity.PetEntity
import br.com.moapetapp.data.local.entity.VaccineEntity

@Database(
    entities = [
        PetEntity::class,
        VaccineEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(MoaPetDatabaseConstructor::class)
abstract class MoaPetDatabase : RoomDatabase() {
    
    abstract fun petDao(): PetDao
    abstract fun vaccineDao(): VaccineDao
    
    companion object {
        const val DATABASE_NAME = "moapetapp.db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MoaPetDatabaseConstructor : RoomDatabaseConstructor<MoaPetDatabase> {
    override fun initialize(): MoaPetDatabase
}
