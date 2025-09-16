package com.example.studymanagement.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studymanagement.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("SELECT * FROM StudySession")
    fun getALlSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM StudySession WHERE sessionSubjectiD = :subjectID")
    fun getRecentSessionForSubject(subjectID: Int): Flow<List<StudySession>>

    @Query("SELECT SUM(duration) FROM studysession")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM StudySession WHERE sessionSubjectiD = :subjectID")
    fun getTotalSessionDurationBySubject(subjectID: Int): Flow<Long>

    @Query("DELETE FROM StudySession WHERE sessionSubjectiD = :subjectID")
    fun deleteSessionsBySubjectID(subjectID: Int)

}