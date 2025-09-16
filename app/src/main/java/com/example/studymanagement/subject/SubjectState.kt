package com.example.studymanagement.subject

import androidx.compose.ui.graphics.Color
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.model.Task

// Storing all the relevant states of the subject screen.
data class SubjectState(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors : List<Color> = Subject.CardColors.random(),
    val studiedHours: Float = 0f,
    val progress: Float = 0f,
    val recentSessions: List<StudySession> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: StudySession? = null,

)
