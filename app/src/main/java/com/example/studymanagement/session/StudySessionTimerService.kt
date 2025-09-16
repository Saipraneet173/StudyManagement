package com.example.studymanagement.session

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Binder
import android.os.Build
import android.os.IBinder
import kotlin.time.Duration.Companion.seconds

import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studymanagement.util.Constants.ACTION_SERVICE_CANCEL

import com.example.studymanagement.util.Constants.ACTION_SERVICE_START
import com.example.studymanagement.util.Constants.ACTION_SERVICE_STOP
import com.example.studymanagement.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.studymanagement.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.studymanagement.util.Constants.NOTIFICATION_ID
import com.example.studymanagement.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timer
import kotlin.time.Duration

@AndroidEntryPoint
class StudySessionTimerService: Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var timer: Timer

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StudySessionTimerBinder()

    var duration: Duration = Duration.ZERO
        private set

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set

    var hours = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
        private set

    var subjectId = mutableStateOf<Int?>(null)

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.action.let {
            when(it){

                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer { hours, minutes, seconds ->
                        updateNotification(hours, minutes, seconds)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()
                }

                ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService() {
        createNotificationChannel()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        } else {
            startForeground(NOTIFICATION_ID, notificationBuilder.build(),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }
    }

//    private fun startForegroundService(){
//        createNotificationChannel()
//        startForeground(NOTIFICATION_ID, notificationBuilder.build())
//    }

    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }



    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String){
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder
                .setContentText("$hours:$minutes:$seconds")
                .build()
        )
    }

    // Function to begin the timer clock and keep updating(incrementing) by 1 second.
    private fun startTimer(
        onTick: (h: String, m: String, s: String) -> Unit
    ){
        currentTimerState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    //Function to stop the timer
    private fun stopTimer(){
        if (this::timer.isInitialized){
            timer.cancel()
        }
        currentTimerState.value = TimerState.STOPPED
    }


    //Function to reset the timer
    private fun cancelTimer(){
        duration = Duration.ZERO
        updateTimeUnits()
        currentTimerState.value = TimerState.IDLE

    }
    // Function to update the hours, minutes and seconds individually.
    private fun updateTimeUnits(){
        duration.toComponents{ hours, minutes, seconds, _ ->
            this@StudySessionTimerService.hours.value = hours.toInt().pad()
            this@StudySessionTimerService.minutes.value = minutes.pad()
            this@StudySessionTimerService.seconds.value = seconds.pad()
        }
    }

    inner class StudySessionTimerBinder: Binder(){
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }
}



enum class TimerState{
    IDLE,
    STARTED,
    STOPPED
}