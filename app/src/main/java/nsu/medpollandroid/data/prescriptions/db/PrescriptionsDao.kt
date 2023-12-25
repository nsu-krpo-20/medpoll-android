package nsu.medpollandroid.data.prescriptions.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionsDao {
    @Query("select * from prescription")
    fun getPrescriptionsGeneralInfo(): Flow<List<PrescriptionEntity>>

    @Transaction
    @Query("select * from prescription where id == :id")
    fun getPrescriptionAllInfo(id: Long): Flow<PrescriptionWithMedsAndMetrics>

    @Transaction
    fun insertAllPrescriptionData(prescriptionData: PrescriptionWithMedsAndMetrics) {
        /*
        In order not to keep meds or metrics that have been deleted on backend
        (cascade deletion is enabled for metrics and meds)
        */
        deletePrescription(prescriptionData.prescription)

        insertPrescription(prescriptionData.prescription)
        insertMedicines(prescriptionData.meds)
        insertMetrics(prescriptionData.metrics)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicines(meds: List<MedicineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMetrics(metrics: List<MetricEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPrescription(prescription: PrescriptionEntity)

    @Delete
    fun deletePrescription(prescription: PrescriptionEntity)
}
