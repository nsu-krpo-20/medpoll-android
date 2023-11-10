package nsu.medpollandroid.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CardDao {
    @Query("select * from card")
    fun getAll(): List<Card>

    @Query("select count(*) from card where url = :apiUrl and uuid = :cardUuid")
    fun countCards(apiUrl: String, cardUuid: String): Int

    @Insert
    fun insert(card: Card): Long

    @Delete
    fun delete(card: Card): Int
}
