package nsu.medpollandroid.utils

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nsu.medpollandroid.data.cards.CardsDatabase
import nsu.medpollandroid.data.prescriptions.db.PrescriptionsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext app: Context) = WorkManager.getInstance(app)

    @Singleton
    @Provides
    fun provideCardsDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        CardsDatabase::class.java,
        name = "cards_db"
    ).build()

    @Singleton
    @Provides
    fun providePrescriptionsDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        PrescriptionsDatabase::class.java,
        name = "prescriptions_db"
    ).build()

    @Singleton
    @Provides
    fun provideCardsDao(db: CardsDatabase) = db.cardDao()

    @Singleton
    @Provides
    fun providePrescriptionsDao(db: PrescriptionsDatabase) = db.prescriptionsDao()
}