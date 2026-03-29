package com.wakeup.app.presentation.alarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakeup.app.core.theme.WakeUpColors
import com.wakeup.app.core.theme.WakeUpTheme
import com.wakeup.app.data.alarm.AlarmReceiver
import com.wakeup.app.data.mission.MissionData
import com.wakeup.app.data.mission.MissionFactory
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlarmRingingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val alarmId = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_ID) ?: return
        val alarmLabel = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: "Alarm"
        val missionType = intent.getStringExtra(AlarmReceiver.EXTRA_MISSION_TYPE)?.let {
            MissionType.valueOf(it)
        } ?: MissionType.MATH
        val missionDifficulty = intent.getStringExtra(AlarmReceiver.EXTRA_MISSION_DIFFICULTY)?.let {
            MissionDifficulty.valueOf(it)
        } ?: MissionDifficulty.EASY
        val strictMode = intent.getBooleanExtra(AlarmReceiver.EXTRA_STRICT_MODE, false)
        val snoozeEnabled = intent.getBooleanExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, true)

        setContent {
            WakeUpTheme {
                AlarmRingingScreen(
                    alarmId = alarmId,
                    alarmLabel = alarmLabel,
                    missionType = missionType,
                    missionDifficulty = missionDifficulty,
                    strictMode = strictMode,
                    snoozeEnabled = snoozeEnabled,
                    onDismiss = { finish() }
                )
            }
        }
    }

    companion object {
        fun createIntent(
            context: Context,
            alarmId: String,
            alarmLabel: String,
            missionType: MissionType,
            missionDifficulty: MissionDifficulty,
            strictMode: Boolean,
            snoozeEnabled: Boolean
        ): Intent {
            return Intent(context, AlarmRingingActivity::class.java).apply {
                putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarmLabel)
                putExtra(AlarmReceiver.EXTRA_MISSION_TYPE, missionType.name)
                putExtra(AlarmReceiver.EXTRA_MISSION_DIFFICULTY, missionDifficulty.name)
                putExtra(AlarmReceiver.EXTRA_STRICT_MODE, strictMode)
                putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, snoozeEnabled)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}

@Composable
fun AlarmRingingScreen(
    alarmId: String,
    alarmLabel: String,
    missionType: MissionType,
    missionDifficulty: MissionDifficulty,
    strictMode: Boolean,
    snoozeEnabled: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showMission by remember { mutableStateOf(false) }
    var snoozeCount by remember { mutableIntStateOf(0) }

    // Generate mission data
    val mission = remember {
        MissionFactory.createMission(missionType, missionDifficulty)
    }
    val missionData = remember { mission.generate() }

    val currentTime = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    if (showMission) {
        MissionScreen(
            missionType = missionType,
            missionDifficulty = missionDifficulty,
            missionData = missionData,
            alarmId = alarmId,
            onMissionComplete = { success ->
                if (success) {
                    // Stop the alarm service
                    context.stopService(
                        Intent(context, com.wakeup.app.data.alarm.AlarmService::class.java)
                    )
                    onDismiss()
                }
            },
            onSnooze = if (snoozeEnabled && !strictMode) {
                {
                    snoozeCount++
                    // Implement snooze logic
                    context.stopService(
                        Intent(context, com.wakeup.app.data.alarm.AlarmService::class.java)
                    )
                    onDismiss()
                }
            } else null
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            WakeUpColors.iosDarkBackground,
                            WakeUpColors.iosPurple.copy(alpha = 0.3f),
                            WakeUpColors.iosDarkBackground
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section - Time
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = WakeUpColors.iosRed.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = WakeUpColors.iosRed
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = currentTime,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = alarmLabel,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Middle section - Mission info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Complete ${missionType.displayName()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = mission.getDescription(),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Difficulty: ${missionDifficulty.displayName()}",
                        fontSize = 14.sp,
                        color = when (missionDifficulty) {
                            MissionDifficulty.EASY -> WakeUpColors.iosGreen
                            MissionDifficulty.MEDIUM -> WakeUpColors.iosOrange
                            MissionDifficulty.HARD -> WakeUpColors.iosRed
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bottom section - Actions
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showMission = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WakeUpColors.iosBlue
                        )
                    ) {
                        Text(
                            text = "Start Mission",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (snoozeEnabled && !strictMode) {
                        Button(
                            onClick = {
                                // Stop service and dismiss
                                context.stopService(
                                    Intent(context, com.wakeup.app.data.alarm.AlarmService::class.java)
                                )
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Snooze (5 min)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
