package com.example.studymanagement.session

import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject

sealed class SessionEvent {

    data class OnRelatedSubjectChange(val subject:Subject) : SessionEvent()

    data class SaveSession(val duration: Long): SessionEvent()

    data class onDeleteSessionButtonClick(val session: StudySession): SessionEvent()

    data object DeleteSession :SessionEvent()

    data object NotifyToUpdateSubject :SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ): SessionEvent()

}