package com.example.studymanagement.domain.repository

import com.example.studymanagement.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int): Task?

    fun getUpComingTaskForSubject(subjectInt: Int): Flow<List<Task>>

    fun getCompletedTaskForSubject(subjectInt: Int): Flow<List<Task>>

    fun getAllUpComingTask(): Flow<List<Task>>
}