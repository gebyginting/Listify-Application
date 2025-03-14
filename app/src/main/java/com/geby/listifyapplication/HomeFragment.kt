package com.geby.listifyapplication

import TaskUpdateWorker
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.geby.listifyapplication.addtask.AddTaskActivity
import com.geby.listifyapplication.categorycards.CategoryCardAdapter
import com.geby.listifyapplication.databinding.FragmentHomeBinding
import com.geby.listifyapplication.listpage.ListActivity
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.user.UserModel
import com.geby.listifyapplication.user.UserPreference
import com.geby.listifyapplication.utils.DateHelper
import com.geby.listifyapplication.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private val mUserPreference: UserPreference by lazy { UserPreference(requireContext()) }
    private lateinit var userModel: UserModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSystemBars()
        scheduleTaskUpdate(requireContext())
        setupTodayTaskList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup UI
        showExistingPreference()
        setTotalTodayTask()
        setUpcomingTask()
        setupCategoryCards()
        setupAddTaskButton()
        setupSeeAllTasksButton()

        return binding.root
    }

    private fun setupSystemBars() {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.apply {
                    hide(WindowInsets.Type.systemBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    private fun setupCategoryCards() {
        binding.rvCategoryCard.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = CategoryCardAdapter { selectedStatus ->
            val intent = Intent(requireContext(), ListActivity::class.java).apply {
                putExtra("TASK_STATUS", selectedStatus)
            }
            startActivity(intent)
        }
        binding.rvCategoryCard.adapter = adapter

        homeViewModel.getCategoryCards().observe(viewLifecycleOwner) { categoryList ->
            adapter.submitList(categoryList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupTodayTaskList() {
        val adapter = TaskCardAdapter { selectedTask ->
            homeViewModel.update(selectedTask)
        }
        binding.rvTodayTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodayTask.adapter = adapter

        homeViewModel.getAllTasksByCategory("On Going").observe(viewLifecycleOwner) { taskList ->
            adapter.submitList(taskList.take(3))
            adapter.notifyDataSetChanged()
            binding.tvNotaskmessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
        }


        // WorkManager observer: Jika Worker selesai, perbarui data
        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData("TaskUpdateWorker")
            .observe(viewLifecycleOwner) { workInfos ->
                if (workInfos.any { it.state == WorkInfo.State.SUCCEEDED }) {
                    homeViewModel.refreshTodayTasks() // Panggil ini untuk update otomatis
                }
            }
    }

    private fun setupAddTaskButton() {
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSeeAllTasksButton() {
        binding.tvButtonseeall.setOnClickListener {
            val intent = Intent(requireContext(), ListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showExistingPreference() {
        userModel = mUserPreference.getUser()
        binding.tvName.text = if (userModel.name.isNotEmpty()) "Hello, ${userModel.name}" else "Tidak Ada"
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalTodayTask() {
        val todayDate = DateHelper.getCurrentDate()

        homeViewModel.getAllTasksByCategory("On Going").observe(viewLifecycleOwner) { taskList ->
            val todayTasks = taskList.filter { task ->
                val taskDate = convertDateFormat(task.date.toString())
                taskDate == todayDate
            }
            binding.tvTotaltodaytask.text = "You have ${todayTasks.size} tasks today"
        }
    }

    private fun convertDateFormat(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpcomingTask() {
        homeViewModel.getAllTasksByCategory("On Going").observe(viewLifecycleOwner) { taskList ->
            val upcomingTask = taskList
                .filter { it.date?.isNotEmpty() == true }
                .minByOrNull { parseDate(it.date!!) }

            binding.tvUpcomingtask.text = upcomingTask?.title ?: "No Upcoming Task"
            binding.tvUpcomingtaskdate.text = DateHelper.formatTime(upcomingTask?.date)
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(dateString) ?: Date(Long.MAX_VALUE)
        } catch (e: Exception) {
            Date(Long.MAX_VALUE)
        }
    }

    private fun scheduleTaskUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<TaskUpdateWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TaskUpdateWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
