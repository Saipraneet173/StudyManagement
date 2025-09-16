package com.example.studymanagement.session

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.repository.SessionRepository
import com.example.studymanagement.domain.repository.SubjectRepository
import com.example.studymanagement.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionScreenViewModel @Inject constructor(
subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
): ViewModel(){

    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ){state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SessionState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent){
        when(event){
            SessionEvent.NotifyToUpdateSubject -> notifyToUpdateSubject()
            SessionEvent.DeleteSession -> deleteSession()
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectID
                    )
                }
            }
            is SessionEvent.SaveSession -> insertSession(event.duration)
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedToSubject,
                        subjectId = event.subjectId
                    )
                }
            }
            is SessionEvent.onDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
        }
    }

    private fun notifyToUpdateSubject() {
        viewModelScope.launch {
            if(state.value.subjectId == null || state.value.relatedToSubject == null){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Please select the subject from dropdown related to this session"
                    )
                )
            }
        }
    }


    // Function to delete the session when the delete icon is clicked
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

    // Function to insert the session into the recent session's list.
    private fun insertSession(duration: Long) {
        viewModelScope.launch {
            if(duration < 36){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "A single session cannot be less than 36 seconds"
                    )
                )
                return@launch
            }
            try {
                sessionRepository.insertSession(
                    session = StudySession(
                        sessionSubjectiD = state.value.subjectId ?: -1,
                        relatedSubject = state.value.relatedToSubject ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration = duration
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Session saved successfully."
                    )
                )
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowBar(
                        message = "Couldn't update session.${e.message}",
                        duration = SnackbarDuration.Long
                    )

                )
            }
        }
    }

}