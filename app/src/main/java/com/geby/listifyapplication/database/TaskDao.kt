package com.geby.listifyapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("Select * from task ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("Select * from task WHERE status = :status ORDER BY id ASC")
    fun getAllTasksByCategory(status: String): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

}