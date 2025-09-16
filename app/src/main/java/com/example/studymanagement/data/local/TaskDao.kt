package com.example.studymanagement.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studymanagement.domain.model.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("DELETE FROM Task WHERE taskID = :taskID")
    suspend fun deleteTask(taskID: Int)

    @Query("DELETE FROM Task WHERE taskSubjectID = :subjectID")
    suspend fun deleteTaskBySubejectid(subjectID: Int)

    @Query("SELECT * FROM Task WHERE taskID = :taskID")
    suspend fun getTaskByID(taskID: Int): Task?

    @Query("SELECT * FROM Task WHERE taskSubjectID = :subjectID")
    fun getTasksForSubjects(subjectID: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task")
    fun getALlTasks(): Flow<List<Task>>
}