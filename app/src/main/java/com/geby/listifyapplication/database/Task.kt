package com.geby.listifyapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Suppress("DEPRECATED_ANNOTATION")
@Entity
class Task (
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int = 0,

    @field:ColumnInfo(name = "name")
    var title: String? = null,

    @field:ColumnInfo(name = "description")
    var description: String? = null,

    @field:ColumnInfo(name = "status")
    var status: String? = "On Going",

    @field:ColumnInfo(name = "date")
    var date: String? = null
)
