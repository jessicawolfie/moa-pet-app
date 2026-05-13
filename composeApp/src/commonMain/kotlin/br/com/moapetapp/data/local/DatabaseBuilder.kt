package br.com.moapetapp.data.local

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<MoaPetDatabase>