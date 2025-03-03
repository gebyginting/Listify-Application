package com.geby.listifyapplication.addtask

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.databinding.ActivityAddTaskBinding
import com.geby.listifyapplication.utils.ViewModelFactory
import com.geby.workoutreminderapp.ui.log.DatePickerFragment
import com.geby.workoutreminderapp.ui.log.TimePickerFragment
import com.geby.workoutreminderapp.utils.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private lateinit var binding: ActivityAddTaskBinding
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var task: Task

    // alarm receiver
    private lateinit var alarmReceiver: AlarmReceiver
    private var scheduledDate = ""
    private var scheduledTime = ""
    private var schedule: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSchedule()
        addTask()
    }

    private fun addTask() {
        with(binding) {
            createButton.setOnClickListener {
                val title = taskNameEditText.text.toString()
                val description = descriptionEditText.text.toString()

                when {
                    title.isEmpty() -> taskNameEditText.error = "Tidak boleh kosong"
                    description.isEmpty() -> descriptionEditText.error = "Tidak boleh kosong"
                    schedule == null -> showToast("Task harus dijadwalkan")
                    else -> {
                        task = Task(
                            title = title,
                            description = description,
                            date = schedule.toString()
                        )

                        homeViewModel.add(task)

                        alarmReceiver.setOneTimeAlarm(
                            context = this@AddTaskActivity,
                            type = AlarmReceiver.TYPE_ONE_TIME,
                            date = scheduledDate,
                            time = scheduledTime,
                            message = "It's time to do your task: $title!"
                        )

                        showToast("Task berhasil ditambahkan")
                        finish()
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

        binding.dateEditText.setText(dateFormat.format(calendar.time))
        scheduledDate = dateFormat.format(calendar.time)
        combineDateTime()
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        // Simpan waktu yang dipilih
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.timeEditText.setText(timeFormat.format(calendar.time))
        scheduledTime = timeFormat.format(calendar.time)

        combineDateTime()
    }

    private fun combineDateTime() {
        if (scheduledDate.isNotEmpty() && scheduledTime.isNotEmpty()) {
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateTimeFormat.timeZone = TimeZone.getDefault()

            val dateTimeString = "$scheduledDate $scheduledTime"
            schedule = dateTimeFormat.parse(dateTimeString)
            Log.d("Schedule", "After UTC Conversion: $schedule")
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }
}