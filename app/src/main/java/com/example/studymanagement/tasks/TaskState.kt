package com.example.studymanagement.tasks

import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.util.Priority

// Used to store all the relevant states in the task screen
data class TaskState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.Low,
    val relatedToSubject: String? =null,
    val subjects: List<Subject> = emptyList(),
    val subjectID: Int? = null,
    val currentTaskID: Int? = null
)
