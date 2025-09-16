package com.example.studymanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Task(
    val title: String,
    val description: String,
    val duedate: Long,
    val priority: Int,
    val relatedSubject: String,
    val isCompleted: Boolean,
    val taskSubjectID: Int,
    @PrimaryKey(autoGenerate = true)
    val taskID: Int? = null

)
