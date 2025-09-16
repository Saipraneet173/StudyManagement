package com.example.studymanagement.util

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.studymanagement.MainActivity
import com.example.studymanagement.session.StudySessionTimerService
import com.example.studymanagement.util.Constants.CLICK_REQUEST_CODE

object ServiceHelper {

    // Function to navigate directly to StudySession Screen when clicked on the timer notification
    fun clickPendingIntent(context: Context): PendingIntent {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "study_manager://dashboard/StudySession".toUri(),
            context,
            MainActivity::class.java
        )
        // Maintain the back stack so that user navigates to the dashboard screen when back button clicked rather than closing the app.
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    // Function to pass actions to the Timer service
    fun triggerForegroundService(context: Context, action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}