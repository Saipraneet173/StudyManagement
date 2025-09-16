package com.example.studymanagement.Dashboard

import androidx.compose.ui.graphics.Color
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject

data class DashboardState(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.CardColors.random(),
    val session: StudySession? = null
    )
