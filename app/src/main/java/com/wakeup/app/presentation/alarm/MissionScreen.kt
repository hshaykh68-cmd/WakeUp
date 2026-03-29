package com.wakeup.app.presentation.alarm

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakeup.app.core.theme.WakeUpColors
import com.wakeup.app.data.mission.MissionData
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType

@Composable
fun MissionScreen(
    missionType: MissionType,
    missionDifficulty: MissionDifficulty,
    missionData: MissionData,
    alarmId: String,
    onMissionComplete: (Boolean) -> Unit,
    onSnooze: (() -> Unit)?
) {
    var userInput by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }
    var showError by remember { mutableStateOf(false) }
    var isComplete by remember { mutableStateOf(false) }
    
    // For memory mission pattern
    val patternInput = remember { mutableStateListOf<Int>() }
    var showPattern by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WakeUpColors.iosDarkBackground,
                        WakeUpColors.iosBlue.copy(alpha = 0.2f),
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
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = missionType.displayName(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Complete to dismiss alarm",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            // Mission Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (missionType) {
                    MissionType.MATH -> MathMissionContent(
                        missionData = missionData,
                        userInput = userInput,
                        onInputChange = { 
                            userInput = it
                            showError = false
                        },
                        showError = showError
                    )
                    MissionType.MEMORY -> MemoryMissionContent(
                        missionData = missionData,
                        patternInput = patternInput,
                        showPattern = showPattern,
                        onShowPattern = { showPattern = it },
                        onPatternComplete = { isCorrect ->
                            if (isCorrect) {
                                isComplete = true
                                onMissionComplete(true)
                            }
                        }
                    )
                    MissionType.TYPING -> TypingMissionContent(
                        missionData = missionData,
                        userInput = userInput,
                        onInputChange = {
                            userInput = it
                            showError = false
                        },
                        showError = showError
                    )
                    else -> MathMissionContent(
                        missionData = missionData,
                        userInput = userInput,
                        onInputChange = { userInput = it },
                        showError = showError
                    )
                }
            }
            
            // Bottom Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (missionType != MissionType.MEMORY) {
                    Button(
                        onClick = {
                            attempts++
                            if (validateMission(missionType, userInput, missionData)) {
                                isComplete = true
                                onMissionComplete(true)
                            } else {
                                showError = true
                                userInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WakeUpColors.iosBlue
                        )
                    ) {
                        Text(
                            text = "Submit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                if (attempts > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Attempt $attempts",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
                
                if (onSnooze != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onSnooze) {
                        Text(
                            text = "Snooze",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MathMissionContent(
    missionData: MissionData,
    userInput: String,
    onInputChange: (String) -> Unit,
    showError: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = missionData.question,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = userInput,
            onValueChange = onInputChange,
            label = { Text("Your answer") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true,
            isError = showError,
            supportingText = if (showError) {
                { Text("Incorrect answer, try again!", color = WakeUpColors.iosRed) }
            } else null
        )
    }
}

@Composable
private fun MemoryMissionContent(
    missionData: MissionData,
    patternInput: MutableList<Int>,
    showPattern: Boolean,
    onShowPattern: (Boolean) -> Unit,
    onPatternComplete: (Boolean) -> Unit
) {
    val targetPattern = missionData.pattern
    
    if (showPattern) {
        // Show the pattern to memorize
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Remember this pattern:",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                targetPattern.forEach { number ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when (number) {
                                    1 -> WakeUpColors.iosRed
                                    2 -> WakeUpColors.iosBlue
                                    3 -> WakeUpColors.iosGreen
                                    4 -> WakeUpColors.iosYellow
                                    else -> WakeUpColors.iosPurple
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { onShowPattern(false) },
                colors = ButtonDefaults.buttonColors(containerColor = WakeUpColors.iosBlue)
            ) {
                Text("I'm Ready")
            }
        }
    } else {
        // Input phase
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Repeat the pattern:",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Show entered pattern
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(targetPattern.size) { index ->
                    val entered = patternInput.getOrNull(index)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (entered != null) {
                                    when (entered) {
                                        1 -> WakeUpColors.iosRed
                                        2 -> WakeUpColors.iosBlue
                                        3 -> WakeUpColors.iosGreen
                                        4 -> WakeUpColors.iosYellow
                                        else -> WakeUpColors.iosPurple
                                    }
                                } else Color.White.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (entered != null) {
                            Text(
                                text = entered.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Number pad
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumberButton(1, WakeUpColors.iosRed, patternInput) { patternInput.add(1) }
                    NumberButton(2, WakeUpColors.iosBlue, patternInput) { patternInput.add(2) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumberButton(3, WakeUpColors.iosGreen, patternInput) { patternInput.add(3) }
                    NumberButton(4, WakeUpColors.iosYellow, patternInput) { patternInput.add(4) }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Check button
            if (patternInput.size == targetPattern.size) {
                val isCorrect = patternInput.toList() == targetPattern
                Button(
                    onClick = { onPatternComplete(isCorrect) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCorrect) WakeUpColors.iosGreen else WakeUpColors.iosRed
                    )
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isCorrect) "Correct!" else "Wrong Pattern")
                }
            }
            
            // Reset button
            TextButton(
                onClick = { 
                    patternInput.clear()
                    onShowPattern(true)
                }
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Show Pattern Again")
            }
        }
    }
}

@Composable
private fun NumberButton(
    number: Int,
    color: Color,
    patternInput: List<Int>,
    onClick: () -> Unit
) {
    val isDisabled = patternInput.size >= 4
    
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.3f)
        ),
        enabled = !isDisabled
    ) {
        Text(
            text = number.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun TypingMissionContent(
    missionData: MissionData,
    userInput: String,
    onInputChange: (String) -> Unit,
    showError: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = missionData.answer,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = userInput,
            onValueChange = onInputChange,
            label = { Text("Type the phrase above") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3,
            isError = showError,
            supportingText = if (showError) {
                { Text("Text doesn't match, try again!", color = WakeUpColors.iosRed) }
            } else null
        )
    }
}

private fun validateMission(type: MissionType, userInput: String, missionData: MissionData): Boolean {
    return when (type) {
        MissionType.MATH -> userInput.trim() == missionData.answer
        MissionType.TYPING -> userInput.trim().lowercase() == missionData.answer.trim().lowercase()
        else -> false
    }
}
