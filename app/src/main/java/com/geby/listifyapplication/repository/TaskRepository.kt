package com.geby.listifyapplication.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.database.TaskDao
import com.geby.listifyapplication.database.TaskRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TaskRepository(application: Application) {
    private val mTasksDao: TaskDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = TaskRoomDatabase.getDatabase(application)
        mTasksDao = db.taskDao()
    }

    fun getAllTasks(): LiveData<List<Task>> = mTasksDao.getAllTasks()

    fun getTaskById(taskId: Int): LiveData<Task> = mTasksDao.getTaskById(taskId)

    fun add(task: Task) {
        executorService.execute { mTasksDao.add(task) }
    }

    fun update(task: Task) {
        executorService.execute { mTasksDao.update(task) }
    }

    fun delete(task: Task) {
        executorService.execute { mTasksDao.delete(task) }
    }


}