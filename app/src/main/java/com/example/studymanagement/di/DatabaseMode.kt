package com.example.studymanagement.di

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import com.example.studymanagement.data.local.Appdatabase
import com.example.studymanagement.data.local.SessionDao
import com.example.studymanagement.data.local.SubjectObject
import com.example.studymanagement.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseMode {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): Appdatabase{
        return Room
            .databaseBuilder(
                application,
                Appdatabase::class.java,
                "studyManagement.db"
            ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: Appdatabase):SubjectObject{
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: Appdatabase):TaskDao{
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: Appdatabase):SessionDao{
        return database.sessionDao()
    }

}