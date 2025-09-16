package com.example.studymanagement.Components

import android.se.omapi.Session
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studymanagement.R
import com.example.studymanagement.domain.model.StudySession
import com.example.studymanagement.domain.model.Task
import com.example.studymanagement.util.Priority
import com.example.studymanagement.util.changeMillistoDateString
import com.example.studymanagement.util.toHours

@Composable
private fun StudysessionCard(
    modifier: Modifier = Modifier,
    session: StudySession,
    onDeleteIcon: () -> Unit,

){
    Card(
        modifier = modifier,
        border = BorderStroke(1.dp,Color.LightGray)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = session.relatedSubject,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = session.date.changeMillistoDateString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${session.duration.toHours()} hr",
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = onDeleteIcon) {
                Icon(
                    imageVector = Icons.Default.Delete ,
                    contentDescription ="Delete Session"
                )
            }
        }
    }
}

fun LazyListScope.StudySessionList(
    sectionTitle: String,
    sessions: List<StudySession>,
    emptyListText: String,
    onDeleteIcon: (StudySession) -> Unit
){
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }

    if(sessions.isEmpty()){
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.lamp),
                    contentDescription = emptyListText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    items(sessions){session ->
        StudysessionCard(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            session = session,
            onDeleteIcon = {onDeleteIcon(session)}
        )

    }
}