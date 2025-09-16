package com.example.studymanagement.domain.repository


import com.example.studymanagement.domain.model.Subject
import kotlinx.coroutines.flow.Flow


interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun deleteSubject(subjectId: Int)

    fun getAllSubjects(): Flow<List<Subject>>

    suspend fun getSubjectsById(subjectId: Int):Subject?
}