package com.example.studymanagement.Dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.domain.repository.SessionRepository
import com.example.studymanagement.domain.repository.SubjectRepository
import com.example.studymanagement.domain.repository.TaskRepository
import com.example.studymanagement.util.SnackbarEvent
import com.example.studymanagement.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration()
    ){state, subjectCount, goalHours, subjects, sessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalGoalHours = goalHours,
            subjects = subjects,
            totalStudiedHours = sessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = DashboardState()
    )

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpComingTask()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentSession: StateFlow<List<StudySession>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

// used to provide actions for the user interactions with the dashboard screen.
    fun onEvent(event:DashboardEvent){
        when(event){
            is DashboardEvent.onSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is DashboardEvent.onGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            is DashboardEvent.onSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.colors)
                }
            }
            is DashboardEvent.onDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(
                        session = event.session
                    )
                }
            }
            DashboardEvent.SaveSubject -> saveSubject()
            DashboardEvent.DeleteSession -> deleteSession()
            is DashboardEvent.onTaskIsCompleteChange -> { updateTask(event.task)}

        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isCompleted = !task.isCompleted)
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(message = "Saved in completed tasks")
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        "Couldn't update task.${e.message}",
                        SnackbarDuration.Long
                    )

                )
            }
        }

    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )

                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.CardColors.random()
                    )
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar("Subject Saved Successfully")
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        "Couldn't save subject.${e.message}",
                        SnackbarDuration.Long
                    )

                )
            }
        }
    }

    private fun deleteSession(){
        viewModelScope.launch {
            try{
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "Session deleted successfully")
                    )
                }
            } catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Couldn't delete session.${e.message}",
                        duration = SnackbarDuration.Long
                    )

                )
            }
        }
    }
}

