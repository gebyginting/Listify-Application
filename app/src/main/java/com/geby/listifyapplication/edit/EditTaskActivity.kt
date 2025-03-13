package com.geby.listifyapplication.edit

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.databinding.ActivityEditTaskBinding
import com.geby.listifyapplication.utils.AlarmReceiver
import com.geby.listifyapplication.utils.DateHelper
import com.geby.listifyapplication.utils.ViewModelFactory
import com.geby.workoutreminderapp.ui.log.DatePickerFragment
import com.geby.workoutreminderapp.ui.log.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private lateinit var binding: ActivityEditTaskBinding
    private var taskId: Int = -1
    private val homeViewModel: HomeViewModel by viewModels { ViewModelFactory.getInstance(application) }

    private lateinit var alarmReceiver: AlarmReceiver
    private var scheduledDate = ""
    private var scheduledTime = ""
    private var schedule: Date? = null
    private var currentTask: Task? = null  // Simpan task saat ini untuk menghindari multiple calls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getIntExtra("TASK_ID", -1)
        alarmReceiver = AlarmReceiver()

        setUpUI()
        observeTask()
    }

    private fun setUpUI() {
        binding.closeButton.setOnClickListener { finish() }
        binding.dateEditText.setOnClickListener { DatePickerFragment().show(supportFragmentManager, DATE_PICKER_TAG) }
        binding.timeEditText.setOnClickListener { TimePickerFragment().show(supportFragmentManager, TIME_PICKER_ONCE_TAG) }
        binding.updateButton.setOnClickListener { updateTask() }
    }

    private fun observeTask() {
        if (taskId != -1) {
            homeViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    currentTask = it  // Simpan task saat ini
                    binding.taskNameEditText.setText(it.title)
                    binding.descriptionEditText.setText(it.description)
                    binding.dateEditText.setText(DateHelper.formatDate(it.date))
                    binding.timeEditText.setText(DateHelper.formatTime(it.date))

                    scheduledDate = DateHelper.formatDate(it.date)
                    scheduledTime = DateHelper.formatTime(it.date)
                }
            }
        }
    }

    private fun updateTask() {
        currentTask?.let {
            val updatedTask = it.copy(
                title = binding.taskNameEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                date = schedule?.toString() ?: it.date
            )

            homeViewModel.update(updatedTask)
            alarmReceiver.setOneTimeAlarm(
                this,
                AlarmReceiver.TYPE_ONE_TIME,
                scheduledDate.ifEmpty { DateHelper.formatDate(it.date) },
                scheduledTime.ifEmpty { DateHelper.formatTime(it.date) },
                "It's time to do your task: ${updatedTask.title}!"
            )
            finish()
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        scheduledDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        binding.dateEditText.setText(scheduledDate)
        combineDateTime()
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        scheduledTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        binding.timeEditText.setText(scheduledTime)
        combineDateTime()
    }

    private fun combineDateTime() {
        if (scheduledDate.isNotEmpty() && scheduledTime.isNotEmpty()) {
            try {
                schedule = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse("$scheduledDate $scheduledTime")
                Log.d("Schedule", "Final Combined Date & Time: $schedule")
            } catch (e: Exception) {
                Log.e("Schedule", "Error parsing date", e)
            }
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }
}