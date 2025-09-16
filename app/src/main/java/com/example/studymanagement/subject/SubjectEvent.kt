package com.example.studymanagement.subject

import androidx.compose.ui.graphics.Color
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Task

// Handles the user events(interactions) in the subject screen.
sealed class SubjectEvent {

    data object UpdateSubject: SubjectEvent()

    data object DeleteSubject: SubjectEvent()

    data object DeleteSession: SubjectEvent()

    data object UpdateProgress: SubjectEvent()

    data class OnTaskIsCompleteChange(val task: Task): SubjectEvent()

    data class OnSubjectCardColorChange(val color: List<Color>): SubjectEvent()

    data class OnSubjectNameChange(val name: String): SubjectEvent()

    data class OnGoalStudyHoursChange(val hours: String): SubjectEvent()

    data class OnDeleteSessionButtonClick(val session: StudySession): SubjectEvent()
}