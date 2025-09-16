package com.example.studymanagement.session

import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<StudySession> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: Int? = null,
    val session: StudySession? = null
)
