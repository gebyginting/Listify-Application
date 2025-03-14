package com.geby.listifyapplication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.geby.listifyapplication.categorycards.CategoryCardData
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.repository.TaskRepository

class HomeViewModel(application: Application) : ViewModel() {

    private val mTaskRepository: TaskRepository = TaskRepository(application)
    private val _todayTasks = MutableLiveData<List<Task>>()
    val todayTasks: LiveData<List<Task>> get() = _todayTasks

    init {
        refreshTodayTasks()
    }

    fun refreshTodayTasks() {
        mTaskRepository.getAllTasksByCategory("On Going").observeForever { taskList ->
            _todayTasks.postValue(taskList.take(3))
        }
    }

    fun getAllTasksByCategory(status: String): LiveData<List<Task>> = mTaskRepository.getAllTasksByCategory(status)

    fun getCategoryCards(): LiveData<List<CategoryCardData>> {
        return mTaskRepository.getAllTasks().map { tasks ->
            val groupedTasks = tasks.groupBy { it.status }
            listOf(
                CategoryCardData(R.drawable.completed_icon, "Completed", groupedTasks["Completed"]?.size ?: 0),
                CategoryCardData(R.drawable.not_done_icon, "Not Done", groupedTasks["Not Done"]?.size ?: 0),
                CategoryCardData(R.drawable.canceled_icon, "Canceled", groupedTasks["Canceled"]?.size ?: 0),
                CategoryCardData(R.drawable.on_going_icon, "On Going", groupedTasks["On Going"]?.size ?: 0)
            )
        }
    }

    fun getTaskById(taskId: Int): LiveData<Task> = mTaskRepository.getTaskById(taskId)

    fun add(task: Task) {
        mTaskRepository.add(task)
    }

    fun update(task: Task) {
        mTaskRepository.update(task)
    }

    fun delete(task: Task) {
        mTaskRepository.delete(task)
    }
}