package com.geby.listifyapplication.edit

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.databinding.ActivityEditTaskBinding
import com.geby.listifyapplication.utils.DateHelper
import com.geby.listifyapplication.utils.ViewModelFactory
import com.geby.workoutreminderapp.ui.log.DatePickerFragment
import com.geby.workoutreminderapp.ui.log.TimePickerFragment
import com.geby.workoutreminderapp.utils.AlarmReceiver
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EditTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private lateinit var binding : ActivityEditTaskBinding
    private var taskId: Int = -1

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    // alarm receiver
    private lateinit var alarmReceiver: AlarmReceiver
    private var scheduledDate = ""
    private var scheduledTime = ""
    private var schedule: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getIntExtra("TASK_ID", -1)

        setUp()
        setSchedule()
        editTask()
    }

    private fun setUp() {
        binding.closeButton.setOnClickListener {
            finish()
        }
        if (taskId != -1) {
            homeViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    with(binding) {
                        taskNameEditText.setText(it.title)
                        descriptionEditText.setText(it.description)
                        dateEditText.setText(DateHelper.formatDate(it.date))
                        timeEditText.setText(DateHelper.formatTime(it.date))

                        scheduledDate = DateHelper.formatDate(it.date)
                        scheduledTime = DateHelper.formatTime(it.date)
                    }
                }
            }
        }
    }

    private fun editTask() {
        with(binding) {
            updateButton.setOnClickListener {
                if (taskId != -1) {
                    homeViewModel.getTaskById(taskId).observe(this@EditTaskActivity) { task ->
                        task?.let {
                            val updatedTask = it.copy(
                                title = taskNameEditText.text.toString(),
                                description = descriptionEditText.text.toString(),
                                date = schedule.toString()
                            )
                            homeViewModel.update(updatedTask)

                            alarmReceiver.setOneTimeAlarm(
                                context = this@EditTaskActivity,
                                type = AlarmReceiver.TYPE_ONE_TIME,
                                date = scheduledDate,
                                time = scheduledTime,
                                message = "It's time to do your task: $title!"
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setSchedule() {
        with(binding) {
            dateEditText.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            timeEditText.setOnClickListener {
                val timePickerFragmentOne = TimePickerFragment()
                timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            alarmReceiver = AlarmReceiver()
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        scheduledDate = dateFormat.format(calendar.time)
        binding.dateEditText.setText(scheduledDate)
        Log.d("Schedule", "Selected Date: $scheduledDate")

        combineDateTime()
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        scheduledTime = timeFormat.format(calendar.time)
        binding.timeEditText.setText(scheduledTime)
        Log.d("Schedule", "Selected Time: $scheduledTime")

        combineDateTime()
    }

    private fun combineDateTime() {
        if (scheduledDate.isNotEmpty() && scheduledTime.isNotEmpty()) {
            val dateTimeString = "$scheduledDate $scheduledTime"
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateTimeFormat.timeZone = TimeZone.getDefault()

            try {
                schedule = dateTimeFormat.parse(dateTimeString)
                Log.d("Schedule", "Final Combined Date & Time: $schedule")
            } catch (e: ParseException) {
                Log.e("Schedule", "Error parsing date: $dateTimeString", e)
            }
        } else {
            Log.e("Schedule", "Scheduled date or time is empty: $scheduledDate, $scheduledTime")
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }
}