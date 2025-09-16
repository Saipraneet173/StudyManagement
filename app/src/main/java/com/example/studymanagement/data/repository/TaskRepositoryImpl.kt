package com.example.studymanagement.data.repository

import com.example.studymanagement.data.local.TaskDao
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    private val taskDao: TaskDao
): TaskRepository{

    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
       return taskDao.getTaskByID(taskId)
    }

    override fun getUpComingTaskForSubject(subjectInt: Int): Flow<List<Task>> {
      return taskDao.getTasksForSubjects(subjectInt)
          .map { tasks -> tasks.filter { it.isCompleted.not() } }
          .map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTaskForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubjects(subjectInt)
            .map { tasks -> tasks.filter { it.isCompleted } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpComingTask(): Flow<List<Task>> {
        return taskDao.getALlTasks()
            .map { tasks -> tasks.filter { it.isCompleted.not() } }
            .map { tasks -> sortTasks(tasks) }
    }

    // Sorting the Tasks based on the due date and priority value.
    private fun sortTasks(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(compareBy<Task> { it.duedate }.thenByDescending { it.priority })
    }
}