package com.example.studymanagement.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymanagement.Components.AddSubjectDialogue
import com.example.studymanagement.Components.CountCard
import com.example.studymanagement.Components.DeleteDialogue
import com.example.studymanagement.Components.StudySessionList
import com.example.studymanagement.Components.taskList
import com.example.studymanagement.destinations.TaskScreenRouteDestination
import com.example.studymanagement.domain.model.Subject


import com.example.studymanagement.tasks.TaskScreenNavArgs
import com.example.studymanagement.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.atan

data class SubjectScreenNavArgs(
    val subjectid: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
){

    val viewModel: SubjectScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SubjectScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackButtonClick = {navigator.navigateUp()},
        onAddTaskButtonClick = {
            val navArgs = TaskScreenNavArgs(taskId = null, subjectId = -1)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))
        },
        onTaskCardClick = {taskid->
            val navArgs = TaskScreenNavArgs(taskId = taskid, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
    state: SubjectState,
    onEvent: (SubjectEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackButtonClick: () -> Unit,
    onAddTaskButtonClick: () -> Unit,
    onTaskCardClick: (Int?)-> Unit
){

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    val isbuttonExpanded by remember{ derivedStateOf { listState.firstVisibleItemIndex == 0 }}

    var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var DeleteSubjectOpen by rememberSaveable { mutableStateOf(false) }
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

                SnackbarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours ){
        onEvent(SubjectEvent.UpdateProgress)
    }

//    var subjectName by remember { mutableStateOf("") }
//    var goalHours by remember{ mutableStateOf("") }
//    var selectedColor by remember{ mutableStateOf(Subject.CardColors.random()) }


    AddSubjectDialogue(
        isOpen = isEditSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubejctNameChange = {onEvent(SubjectEvent.OnSubjectNameChange(it))} ,
        onGoalHoursChange ={onEvent(SubjectEvent.OnGoalStudyHoursChange(it))} ,
        selectedColors = state.subjectCardColors,
        onColorChange = {onEvent(SubjectEvent.OnSubjectCardColorChange(it))},
        onDismissRequest = { isEditSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialogOpen = false
        }
    )

    DeleteDialogue(
        isOpen = DeleteSubjectOpen,
        Title = "Delete Subject?",
        bodyText ="Are you sure you want to delete this subject? All related tasks and study sessions will be permanently removed. This action cannot be undone.",
        onDismissRequest = { DeleteSubjectOpen = false},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            DeleteSubjectOpen = false

        }
    )
    DeleteDialogue(
        isOpen = DeleteDialogueOpen,
        Title = "Delete Session?",
        bodyText ="Are you sure you want to delete this session? Your studied hours will be reduced by the amount of hours of this session time. This action cannot be undone.",
        onDismissRequest = { DeleteDialogueOpen = false},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSession)
            DeleteDialogueOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = state.subjectName,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = { DeleteSubjectOpen = true},
                oneEditButtonClick = {isEditSubjectDialogOpen= true},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add")},
                text ={ Text(text = "Add Task")},
                expanded = isbuttonExpanded
            )
        }

    ) {paddingValues -> 
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp) ,
                    studiedHours = state.goalStudyHours ,
                    goalHours = state.studiedHours.toString(),
                    progress = state.progress
                )
            }
            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any tasks to compelete.\n " + "Click the + button in subject screen to add new tasks",
                tasks = state.upcomingTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                ontaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            taskList(
                sectionTitle = "Completed Task",
                emptyListText = "You don't have any completed tasks.\n " + "Click the checkbox on completion of the task.",
                tasks = state.completedTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                ontaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            StudySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session. \n" + "Start a session to begin recording your progress",
                sessions = state.recentSessions,
                onDeleteIcon = {
                    DeleteDialogueOpen = true
                    onEvent(SubjectEvent.OnDeleteSessionButtonClick(it))
                }
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    oneEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior

){
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
                         IconButton(onClick =  onBackButtonClick ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription ="Navigation back"
                            )
                             
                         }
        },
        title = { Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall
        )},
        actions = {
            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription ="Default Subject"
                )

            }
            IconButton(onClick = oneEditButtonClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription ="Edit Subject"
                )

            }
        }
    )
}



@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float
){
    val percentageProgess = remember(progress){
        (progress*100).toInt().coerceIn(0,100)
    }
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        CountCard(
            modifier = Modifier.weight(1f),
            heading = "Studied Hours",
            counter = goalHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            heading = "Goal Study Hours",
            counter = studiedHours
        )
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = "$percentageProgess%")
        }

    }
}