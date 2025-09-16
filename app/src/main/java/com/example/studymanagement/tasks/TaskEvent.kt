package com.example.studymanagement.tasks

import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.util.Priority

sealed class TaskEvent {

    data class OnTitleChange(val title: String): TaskEvent()

    data class OnDescriptionChange(val description: String): TaskEvent()

    data class OnDateChange(val millis: Long?): TaskEvent()

    data class OnPriorityChange(val priority: Priority): TaskEvent()

    data class OnRelatedSubjectSelect(val subject: Subject): TaskEvent()

    data object OnIsCompleteChange: TaskEvent()

    data object SaveTask: TaskEvent()

    data object DeleteTask: TaskEvent()
}