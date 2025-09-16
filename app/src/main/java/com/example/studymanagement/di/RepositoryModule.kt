package com.example.studymanagement.di

import com.example.studymanagement.data.repository.SessionRepositoryImplementation
import com.example.studymanagement.data.repository.SubjectRepositoryImplementation
import com.example.studymanagement.data.repository.TaskRepositoryImpl
import com.example.studymanagement.domain.repository.SessionRepository
import com.example.studymanagement.domain.repository.SubjectRepository
import com.example.studymanagement.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImplementation
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImplementation
    ): SessionRepository
}