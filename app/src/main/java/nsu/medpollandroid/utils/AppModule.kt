package nsu.medpollandroid.utils

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.data.cards.CardsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        CardsDatabase::class.java,
        name = "cards_db"
    ).build()

    @Singleton
    @Provides
    fun provideDao(db: CardsDatabase) = db.cardDao()
}