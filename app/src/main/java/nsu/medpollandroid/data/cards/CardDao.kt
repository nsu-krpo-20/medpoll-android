package nsu.medpollandroid.data.cards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("select * from card")
    fun getAll(): Flow<List<Card>>

    @Insert
    suspend fun insert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}
