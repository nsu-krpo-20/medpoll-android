package nsu.medpollandroid.data.prescriptions.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PrescriptionEntity::class, MedicineEntity::class, MetricEntity::class], version = 1)
abstract class PrescriptionsDatabase : RoomDatabase() {
    abstract fun prescriptionsDao(): PrescriptionsDao
}