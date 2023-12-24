package nsu.medpollandroid

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import nsu.medpollandroid.repositories.IRepositories
//import nsu.medpollandroid.utils.MedpollNotificationsManager
import nsu.medpollandroid.utils.Production
import javax.inject.Inject


@HiltAndroidApp
class MedpollApplication : Application() {
    @Production
    @Inject lateinit var repositories: IRepositories

    //@Inject lateinit var medpollNotificationsManager: MedpollNotificationsManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "MEDPOLL_NOTIFICATIONS_CHANNEL"
    }
}