
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.geby.listifyapplication.database.TaskRoomDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class TaskUpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val db = TaskRoomDatabase.getDatabase(applicationContext)
        val taskDao = db.taskDao()

        val currentDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        taskDao.updateExpiredTasks(currentDate.toString())
        Log.d("Current Date: ", currentDate.toString())
        applicationContext.contentResolver.notifyChange(Uri.parse("content://task-updated"), null)

        return Result.success()
    }
}
