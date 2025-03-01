package com.geby.listifyapplication.addtask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.MainActivity
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
    private lateinit var homeViewModel: HomeViewModel
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

        homeViewModel = obtainViewModel(this@AddTaskActivity)

//        binding.createButton.setOnClickListener {
//
//            task = (task ?: Task()).apply {
//                title = "Dummy task 1"
//                description = "Dummy description 1"
//                status = "On Going"
//                date = DateHelper.getCurrentDate()
//            }
//            homeViewModel.add(task as Task)
//            showToast("Task berhasil ditambahkan")
//        }

        setSchedule()
        addTask()
    }

    private fun addTask() {
        with(binding) {
            createButton.setOnClickListener {
                val title = taskNameEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val status = "On Going"

                when {
                    title.isEmpty() -> taskNameEditText.error = "Tidak boleh kosong"
                    description.isEmpty() -> descriptionEditText.error = "Tidak boleh kosong"
                    schedule == null -> showToast("Task harus dijadwalkan") // Cek schedule kosong
                    else -> {
                        if (!::task.isInitialized) {
                            task = Task(0, title, description, status, schedule.toString())
                        } else {
                            task.apply {
                                this.title = title
                                this.description = description
                                this.status = status
                                this.date = schedule.toString()
                            }
                        }

                        homeViewModel.add(task)

                        alarmReceiver.setOneTimeAlarm(
                            context = this@AddTaskActivity,
                            type = AlarmReceiver.TYPE_ONE_TIME,
                            date = scheduledDate,
                            time = scheduledTime,
                            message = "It's time to do your task: $title!"
                        )

                        showToast("Task berhasil ditambahkan")
                        val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): HomeViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HomeViewModel::class.java]
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
            dateTimeFormat.timeZone = TimeZone.getDefault() // Atur timezone ke default (lokal)

            val dateTimeString = "$scheduledDate $scheduledTime"
            schedule = dateTimeFormat.parse(dateTimeString)
            Log.d("Schedule", "After UTC Conversion: $schedule")
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }
}