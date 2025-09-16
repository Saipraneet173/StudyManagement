package com.example.studymanagement.data.repository

import com.example.studymanagement.data.local.SessionDao
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.repository.SessionRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImplementation @Inject constructor(
    private val sessionDao: SessionDao
): SessionRepository{

    override suspend fun insertSession (session: StudySession) {
       sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: StudySession) {
       sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getALlSessions()
            //Used to order the sessions in the order of the date they were created.
            .map { sessions -> sessions.sortedByDescending { it.date } }
    }

    override fun getRecentFiveSessions(): Flow<List<StudySession>> {
        return sessionDao.getALlSessions()
            .map { sessions -> sessions.sortedByDescending { it.date } }
            .take(count = 5)

    }

    override fun getRecentTenSession(subjectId: Int): Flow<List<StudySession>> {
        return sessionDao.getRecentSessionForSubject(subjectId)
            .map { sessions -> sessions.sortedByDescending { it.date } }
            .take(count = 10)

    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubject(subjectId)
    }
}