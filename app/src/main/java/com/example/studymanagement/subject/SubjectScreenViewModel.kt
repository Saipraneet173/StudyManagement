package com.example.studymanagement.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.domain.repository.SessionRepository
import com.example.studymanagement.domain.repository.SubjectRepository
import com.example.studymanagement.domain.repository.TaskRepository
import com.example.studymanagement.navArgs
import com.example.studymanagement.util.SnackbarEvent
import com.example.studymanagement.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SubjectScreenViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()


    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpComingTaskForSubject(navArgs.subjectid),
        taskRepository.getCompletedTaskForSubject(navArgs.subjectid),
        sessionRepository.getRecentTenSession(navArgs.subjectid),
        sessionRepository.getTotalSessionDurationBySubject(navArgs.subjectid)
    ){state, upcomingTasks, completedTask, recentSessions, totalSessionDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){
        when(event){
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SubjectEvent.OnTaskIsCompleteChange -> updateTask(event.task)
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull()?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours/goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }

    // Function to perform the updation of the subject when clicked on the update icon.
    private fun updateSubject() {
        viewModelScope.launch {
            try{
            subjectRepository.upsertSubject(
                subject = Subject(
                    subjectID = state.value.currentSubjectId,
                    name = state.value.subjectName,
                    goalHours = state.value.goalStudyHours.toFloatOrNull()?: 1f,
                    colors = state.value.subjectCardColors.map { it.toArgb() }
                )
            )
            _snackbarEventFlow.emit(
                SnackbarEvent.ShowBar(
                    message = "Subject updated successfully."
                )
            )
        } catch (e: Exception){
            _snackbarEventFlow.emit(
                SnackbarEvent.ShowBar(
                    message = "Couldn't update the subject.${e.message}",
                    SnackbarDuration.Long
                )
            )

            }        }
    }

    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository
                .getSubjectsById(navArgs.subjectid)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it) },
                            currentSubjectId = subject.subjectID
                        )
                    }
                }
        }
    }

    // Function to perform the delete operating on the subject when delete icon clicked.
    private fun deleteSubject(){
        viewModelScope.launch {
            try{
                val currentSubjectID = state.value.currentSubjectId
                if(currentSubjectID != null){
                    withContext(Dispatchers.IO){
                        subjectRepository.deleteSubject(subjectId = currentSubjectID)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "Subject deleted successfully")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "No subject to delete")
                    )
                }

            } catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Couldn't delete the subject.${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isCompleted = !task.isCompleted)
                )
                if(task.isCompleted) {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "Saved in upcoming tasks")
                    )
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "Saved in completed tasks")
                    )
                }
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