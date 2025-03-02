package com.geby.listifyapplication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.repository.TaskRepository

class HomeViewModel(application: Application) : ViewModel() {

    private val mTaskRepository: TaskRepository = TaskRepository(application)

    fun getAllTasks(): LiveData<List<Task>> = mTaskRepository.getAllTasks()

    fun getTaskById(taskId: Int): LiveData<Task> = mTaskRepository.getTaskById(taskId)

    fun add(task: Task) {
        mTaskRepository.add(task)
    }
}