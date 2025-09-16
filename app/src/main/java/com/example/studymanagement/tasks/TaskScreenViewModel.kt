package com.example.studymanagement.tasks


import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.domain.repository.SubjectRepository
import com.example.studymanagement.domain.repository.TaskRepository
import com.example.studymanagement.navArgs
import com.example.studymanagement.util.Priority
import com.example.studymanagement.util.SnackbarEvent
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
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ){ _state, subjects ->
        _state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvent){
        when(event){
            is TaskEvent.OnTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }
            is TaskEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = event.description)
                }
            }
            is TaskEvent.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }
            is TaskEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }
            TaskEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }
            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectID = event.subject.subjectID
                    )
                }
            }
            TaskEvent.SaveTask -> saveTask()
            TaskEvent.DeleteTask -> deleteTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTaskID = state.value.currentTaskID
                if (currentTaskID != null) {
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(taskId = currentTaskID)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "Task deleted successfully")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowBar(message = "No Task to delete")
                    )
                }

            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Couldn't delete the task.${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveTask() {
       viewModelScope.launch {
           val state = state.value
           if(state.subjectID == null || state.relatedToSubject == null){
               _snackbarEventFlow.emit(
                   SnackbarEvent.ShowBar(
                       "Please select subject related to the task ",
                       SnackbarDuration.Long
                   )

               )
               return@launch
           }

           try {
               taskRepository.upsertTask(
                   task = Task(
                       title = state.title,
                       description = state.description,
                       duedate = state.dueDate ?: Instant.now().toEpochMilli(),
                       relatedSubject = state.relatedToSubject,
                       priority = state.priority.value,
                       isCompleted = state.isTaskComplete,
                       taskSubjectID = state.subjectID,
                       taskID = state.currentTaskID
                   )
               )
               _snackbarEventFlow.emit(
                   SnackbarEvent.ShowBar(message = "Task saved successfully")
               )
               _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
           }catch (e: Exception){
               _snackbarEventFlow.emit(
                   SnackbarEvent.ShowBar(
                       message = "Couldn't save task.${e.message}",
                       SnackbarDuration.Long
                   )

               )
           }
       }
    }
    private fun fetchTask(){
        viewModelScope.launch {
            navArgs.taskId?.let { id->
                taskRepository.getTaskById(id)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.duedate,
                            isTaskComplete = task.isCompleted,
                            relatedToSubject = task.relatedSubject,
                            priority = Priority.fromInt(task.priority),
                            subjectID = task.taskSubjectID,
                            currentTaskID = task.taskID
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject(){
        viewModelScope.launch {
            navArgs.subjectId?.let { id ->
                subjectRepository.getSubjectsById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectID = subject.subjectID,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}