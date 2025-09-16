package com.example.studymanagement.data.repository

import com.example.studymanagement.data.local.SessionDao
import com.example.studymanagement.data.local.SubjectObject
import com.example.studymanagement.data.local.TaskDao
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.repository.SubjectRepository

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SubjectRepositoryImplementation @Inject constructor(
    private val subjectDao: SubjectObject,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubeject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
       return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalHours()
    }

    override suspend fun deleteSubject(subjectId: Int){
        taskDao.deleteTaskBySubejectid(subjectId)
        sessionDao.deleteSessionsBySubjectID(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    override suspend fun getSubjectsById(subjectId: Int): Subject? {
        return subjectDao.getSubjectByID(subjectId)
    }
}