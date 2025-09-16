package com.example.studymanagement.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymanagement.Components.AddSubjectDialogue
import com.example.studymanagement.Components.CountCard
import com.example.studymanagement.Components.DeleteDialogue
import com.example.studymanagement.Components.StudySessionList
import com.example.studymanagement.Components.SubjectCard
import com.example.studymanagement.Components.taskList
import com.example.studymanagement.R
import com.example.studymanagement.destinations.SessionScreenRouteDestination
import com.example.studymanagement.destinations.SubjectScreenRouteDestination
import com.example.studymanagement.destinations.TaskScreenRouteDestination
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Subject
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.subject.SubjectScreenNavArgs
import com.example.studymanagement.tasks.TaskScreenNavArgs
import com.example.studymanagement.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Destination(start = true)
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
) {

    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentStudySession by viewModel.recentSession.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        tasks = tasks,
        recentStudySession = recentStudySession,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onSubjectCardClick = { subjectid ->
            subjectid?.let {
                val navArgs = SubjectScreenNavArgs(subjectid = subjectid)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArgs))
            }
        },
        onTaskCardClick = { taskid ->
            val navArgs = TaskScreenNavArgs(taskId = taskid, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))
        },
        onStartSessionClick = {
            navigator.navigate(SessionScreenRouteDestination())
        }
    )
}


@Composable
private fun DashboardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentStudySession: List<StudySession>,
    onEvent: (DashboardEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionClick: () -> Unit
) {


    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var DeleteDialogueOpen by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true){
        snackbarEvent.collectLatest { event->
            when(event){
                is SnackbarEvent.ShowBar ->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialogue(
        isOpen = isAddSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubejctNameChange = { onEvent(DashboardEvent.onSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(DashboardEvent.onGoalStudyHoursChange(it)) },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(DashboardEvent.onSubjectCardColorChange(it)) },
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogOpen = false
        }
    )

    DeleteDialogue(
        isOpen = DeleteDialogueOpen,
        Title = "Delete Session?",
        bodyText = "Are you sure you want to delete this session? Your studied hours will be reduced by the amount of hours of this session time. This action cannot be undone.",
        onDismissRequest = { DeleteDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            DeleteDialogueOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = { DashboardScreenTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    GoalHours = state.totalGoalHours.toString(),
                    StudiedHours = state.totalStudiedHours.toString()
                )
            }
            item {
                SubjectCardsArea(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    emptyText = "You don't have any subjects as of now.\n Click the '+' button to add a new subject.",
                    onAddIconClicked = { isAddSubjectDialogOpen = true },
                    onSubjectCardClick = onSubjectCardClick
                )
            }
            item {
                Button(
                    onClick = { onStartSessionClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start Study Session")
                }
            }
            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any tasks to compelete.\n " + "Click the + button in subject screen to add new tasks",
                tasks = tasks,
                onCheckBoxClick = { onEvent(DashboardEvent.onTaskIsCompleteChange(it)) },
                ontaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            StudySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session. \n" + "Start a session to begin recording your progress",
                sessions = recentStudySession,
                onDeleteIcon = {
                    onEvent(DashboardEvent.onDeleteSessionButtonClick(it))
                    DeleteDialogueOpen = true
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudyManager",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardSection(
    modifier: Modifier,
    subjectCount: Int,
    GoalHours: String,
    StudiedHours: String
) {
    Row(modifier) {
        CountCard(
            modifier = Modifier.weight(1f),
            heading = "Subjects",
            counter = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            heading = "StudiedHours",
            counter = StudiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            heading = "GoalHours",
            counter = GoalHours
        )
    }
}

@Composable
private fun SubjectCardsArea(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyText: String,
    onAddIconClicked: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp),
            )
            IconButton(onClick = onAddIconClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add subject"
                )

            }
        }
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.books),
                contentDescription = emptyText
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { Subject ->
                SubjectCard(
                    subjectName = Subject.name,
                    gradientColors = Subject.colors.map { Color(it) },
                    onClick = { onSubjectCardClick(Subject.subjectID) }
                )
            }
        }
    }
}