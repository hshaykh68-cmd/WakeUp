package com.wakeup.app.presentation.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmScreen(
    viewModel: CreateAlarmViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var hour by remember { mutableStateOf(7) }
    var minute by remember { mutableStateOf(0) }
    var label by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var missionType by remember { mutableStateOf(MissionType.MATH) }
    var missionDifficulty by remember { mutableStateOf(MissionDifficulty.EASY) }
    var strictMode by remember { mutableStateOf(false) }
    var useVibration by remember { mutableStateOf(true) }
    var gradualVolume by remember { mutableStateOf(true) }
    var snoozeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Alarm",
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
                            viewModel.createAlarm(
                                hour = hour,
                                minute = minute,
                                label = label.ifBlank { "Alarm" },
                                repeatDays = selectedDays.toList(),
                                missionType = missionType,
                                missionDifficulty = missionDifficulty,
                                strictMode = strictMode,
                                useVibration = useVibration,
                                gradualVolume = gradualVolume,
                                snoozeEnabled = snoozeEnabled
                            )
                            onNavigateBack()
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
            // Time Picker Section
            TimePickerSection(
                hour = hour,
                minute = minute,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it }
            )

            // Label Input
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                placeholder = { Text("e.g., Work, Gym") },
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

            // Mission Selection
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

            // Save Button
            Button(
                onClick = {
                    viewModel.createAlarm(
                        hour = hour,
                        minute = minute,
                        label = label.ifBlank { "Alarm" },
                        repeatDays = selectedDays.toList(),
                        missionType = missionType,
                        missionDifficulty = missionDifficulty,
                        strictMode = strictMode,
                        useVibration = useVibration,
                        gradualVolume = gradualVolume,
                        snoozeEnabled = snoozeEnabled
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = WakeUpColors.iosBlue
                )
            ) {
                Text(
                    text = "Create Alarm",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimePickerSection(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val displayTime = LocalTime.of(hour, minute).format(timeFormatter)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayTime,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour selector
            NumberSelector(
                value = hour,
                onIncrement = { onHourChange((hour + 1) % 24) },
                onDecrement = { onHourChange((hour - 1 + 24) % 24) },
                label = "Hour"
            )
            
            Text(
                text = ":",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Minute selector
            NumberSelector(
                value = minute,
                onIncrement = { onMinuteChange((minute + 5) % 60) },
                onDecrement = { onMinuteChange((minute - 5 + 60) % 60) },
                label = "Minute"
            )
        }
    }
}

@Composable
fun NumberSelector(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onIncrement) {
            Text("▲", style = MaterialTheme.typography.titleLarge)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onDecrement) {
            Text("▼", style = MaterialTheme.typography.titleLarge)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatDaysSection(
    selectedDays: Set<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit
) {
    Column {
        Text(
            text = "Repeat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.values().forEach { day ->
                val isSelected = selectedDays.contains(day)
                val dayLabel = day.name.take(1)
                
                androidx.compose.material3.FilterChip(
                    selected = isSelected,
                    onClick = { onToggleDay(day) },
                    label = {
                        Text(
                            dayLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WakeUpColors.iosBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionSection(
    selectedType: MissionType,
    selectedDifficulty: MissionDifficulty,
    onTypeSelect: (MissionType) -> Unit,
    onDifficultySelect: (MissionDifficulty) -> Unit
) {
    Column {
        Text(
            text = "Wake Mission",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Mission Type Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(MissionType.MATH, MissionType.MEMORY, MissionType.TYPING).forEach { type ->
                androidx.compose.material3.FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelect(type) },
                    label = { Text(type.displayName()) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Difficulty Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MissionDifficulty.values().forEach { difficulty ->
                val color = when (difficulty) {
                    MissionDifficulty.EASY -> WakeUpColors.iosGreen
                    MissionDifficulty.MEDIUM -> WakeUpColors.iosOrange
                    MissionDifficulty.HARD -> WakeUpColors.iosRed
                }
                
                androidx.compose.material3.FilterChip(
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelect(difficulty) },
                    label = { Text(difficulty.displayName()) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun OptionsSection(
    strictMode: Boolean,
    onStrictModeChange: (Boolean) -> Unit,
    useVibration: Boolean,
    onVibrationChange: (Boolean) -> Unit,
    gradualVolume: Boolean,
    onGradualVolumeChange: (Boolean) -> Unit,
    snoozeEnabled: Boolean,
    onSnoozeChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        OptionSwitch(
            label = "Strict Mode",
            description = "Must complete mission to dismiss",
            checked = strictMode,
            onCheckedChange = onStrictModeChange
        )
        
        OptionSwitch(
            label = "Vibration",
            description = "Vibrate when alarm rings",
            checked = useVibration,
            onCheckedChange = onVibrationChange
        )
        
        OptionSwitch(
            label = "Gradual Volume",
            description = "Volume increases slowly",
            checked = gradualVolume,
            onCheckedChange = onGradualVolumeChange
        )
        
        OptionSwitch(
            label = "Snooze",
            description = "Allow snoozing the alarm",
            checked = snoozeEnabled,
            onCheckedChange = onSnoozeChange
        )
    }
}

@Composable
fun OptionSwitch(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
