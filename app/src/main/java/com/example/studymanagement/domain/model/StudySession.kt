package com.example.studymanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class StudySession(
    val sessionSubjectiD: Int,
    val relatedSubject: String,
    val date: Long,
    val duration: Long,
    @PrimaryKey(autoGenerate = true)
    val sessionID: Int? = null
){

}
