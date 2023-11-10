package nsu.medpollandroid.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Card::class], version = 1)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        private var instance: CardsDatabase? = null;
        @Synchronized fun getCardsDatabase(context: Context): CardsDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context = context,
                    klass = CardsDatabase::class.java,
                    name = "cards_db"
                ).build()
            }
            return instance!!
        }
    }
}