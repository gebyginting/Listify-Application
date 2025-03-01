package com.geby.listifyapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(task: Task)

//    @Update
//    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("Select * from task ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<Task>>
}