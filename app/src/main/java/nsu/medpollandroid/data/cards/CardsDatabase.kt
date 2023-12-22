package nsu.medpollandroid.data.cards

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import nsu.medpollandroid.MedpollApplication

@Database(entities = [Card::class], version = 1)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}