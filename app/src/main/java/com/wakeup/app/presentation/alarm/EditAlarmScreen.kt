package com.wakeup.app.presentation.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wakeup.app.core.theme.WakeUpColors
import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmScreen(
    alarmId: String,
    viewModel: EditAlarmViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val alarm by viewModel.alarm.collectAsState()

    LaunchedEffect(alarmId) {
        viewModel.loadAlarm(alarmId)
    }

    alarm?.let { currentAlarm ->
        EditAlarmContent(
            alarm = currentAlarm,
            onSave = { updatedAlarm ->
                viewModel.updateAlarm(updatedAlarm)
                onNavigateBack()
            },
            onDelete = {
                viewModel.deleteAlarm(alarmId)
                onNavigateBack()
            },
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAlarmContent(
    alarm: Alarm,
    onSave: (Alarm) -> Unit,
    onDelete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var hour by remember { mutableStateOf(alarm.hour) }
    var minute by remember { mutableStateOf(alarm.minute) }
    var label by remember { mutableStateOf(alarm.label) }
    var selectedDays by remember { mutableStateOf(alarm.repeatDays.toSet()) }
    var missionType by remember { mutableStateOf(alarm.missionType) }
    var missionDifficulty by remember { mutableStateOf(alarm.missionDifficulty) }
    var strictMode by remember { mutableStateOf(alarm.strictMode) }
    var useVibration by remember { mutableStateOf(alarm.useVibration) }
    var gradualVolume by remember { mutableStateOf(alarm.gradualVolume) }
    var snoozeEnabled by remember { mutableStateOf(alarm.snoozeEnabled) }
    var isEnabled by remember { mutableStateOf(alarm.isEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Alarm",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onSave(
                                alarm.copy(
                                    hour = hour,
                                    minute = minute,
                                    label = label,
                                    repeatDays = selectedDays.toList(),
                                    missionType = missionType,
                                    missionDifficulty = missionDifficulty,
                                    strictMode = strictMode,
                                    useVibration = useVibration,
                                    gradualVolume = gradualVolume,
                                    snoozeEnabled = snoozeEnabled,
                                    isEnabled = isEnabled
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge,
                            color = WakeUpColors.iosBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Enabled Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alarm Enabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it }
                )
            }

            // Time Picker
            TimePickerSection(
                hour = hour,
                minute = minute,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it }
            )

            // Label
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Repeat Days
            RepeatDaysSection(
                selectedDays = selectedDays,
                onToggleDay = { day ->
                    selectedDays = if (selectedDays.contains(day)) {
                        selectedDays - day
                    } else {
                        selectedDays + day
                    }
                }
            )

            // Mission
            MissionSection(
                selectedType = missionType,
                selectedDifficulty = missionDifficulty,
                onTypeSelect = { missionType = it },
                onDifficultySelect = { missionDifficulty = it }
            )

            // Options
            OptionsSection(
                strictMode = strictMode,
                onStrictModeChange = { strictMode = it },
                useVibration = useVibration,
                onVibrationChange = { useVibration = it },
                gradualVolume = gradualVolume,
                onGradualVolumeChange = { gradualVolume = it },
                snoozeEnabled = snoozeEnabled,
                onSnoozeChange = { snoozeEnabled = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Delete Button
            Button(
                onClick = onDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = WakeUpColors.iosRed.copy(alpha = 0.1f),
                    contentColor = WakeUpColors.iosRed
                )
            ) {
                Text(
                    text = "Delete Alarm",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
