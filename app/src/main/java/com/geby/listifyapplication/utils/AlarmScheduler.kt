package com.geby.listifyapplication.utils
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.geby.listifyapplication.database.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmScheduler {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    fun resetAlarms(context: Context, taskList: List<Task>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (task in taskList) {
            val calendar = Calendar.getInstance()

            try {
                // Konversi scheduledDate dari String ke Date
                val date = task.date?.let { dateFormat.parse(it) }
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Tangani kesalahan parsing jika diperlukan
                e.printStackTrace()
                continue
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(AlarmReceiver.EXTRA_MESSAGE, "It's time for your workout: ${task.title}!")
            intent.putExtra(AlarmReceiver.EXTRA_TYPE, AlarmReceiver.TYPE_ONE_TIME)

            val requestCode = task.id.hashCode()  // ID unik dari task
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Log.d("AlarmScheduler", "Alarm set for workout '${task.title}' at ${calendar.time}")

        }
    }
}
