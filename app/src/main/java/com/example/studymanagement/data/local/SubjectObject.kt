package com.example.studymanagement.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studymanagement.domain.model.Subject
import kotlinx.coroutines.flow.Flow


@Dao
interface SubjectObject {

    @Upsert
    suspend fun upsertSubeject(subject: Subject)

    @Query("SELECT COUNT(*) FROM SUBJECT")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHours) FROM SUBJECT")
    fun getTotalHours(): Flow<Float>

    @Query("SELECT * FROM Subject WHERE subjectID = :subjectID")
    suspend fun getSubjectByID(subjectID: Int): Subject?

    @Query("DELETE FROM Subject WHERE subjectID = :subjectID")
    suspend fun deleteSubject(subjectID: Int)

    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>
}