package nsu.medpollandroid.data.cards

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Card::class], version = 1)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}