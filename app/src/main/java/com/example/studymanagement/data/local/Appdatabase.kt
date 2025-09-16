package com.example.studymanagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.model.Task

@Database(
    entities = [Subject::class, StudySession::class, Task::class],
    version = 1
)

@TypeConverters(ColorListConverter::class)
abstract class Appdatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectObject

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao
}