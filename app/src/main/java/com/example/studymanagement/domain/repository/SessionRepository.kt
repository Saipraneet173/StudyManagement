package com.example.studymanagement.domain.repository

import com.example.studymanagement.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: StudySession)

    suspend fun deleteSession(session: StudySession)

    fun getAllSessions(): Flow<List<StudySession>>

    fun getRecentFiveSessions(): Flow<List<StudySession>>

    fun getRecentTenSession(subjectId: Int): Flow<List<StudySession>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long>

}