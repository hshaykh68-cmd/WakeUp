package com.wakeup.app.domain.model

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val repeatDays: List<DayOfWeek> = emptyList(),
    val soundUri: String? = null,
    val useVibration: Boolean = true,
    val gradualVolume: Boolean = true,
    val missionType: MissionType = MissionType.MATH,
    val missionDifficulty: MissionDifficulty = MissionDifficulty.EASY,
    val strictMode: Boolean = false,
    val snoozeEnabled: Boolean = true,
    val snoozeInterval: Int = 5, // minutes
    val maxSnoozes: Int = 3,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getNextRingTime(): LocalDateTime {
        val now = LocalDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        
        if (next.isBefore(now) || next.isEqual(now)) {
            next = next.plusDays(1)
        }
        
        if (repeatDays.isNotEmpty()) {
            while (!repeatDays.contains(next.dayOfWeek)) {
                next = next.plusDays(1)
            }
        }
        
        return next
    }
    
    fun formattedTime(): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val displayMinute = minute.toString().padStart(2, '0')
        return "$displayHour:$displayMinute $amPm"
    }
}

enum class MissionType {
    MATH,
    MEMORY,
    TYPING,
    SHAKE;
    
    fun displayName(): String = when (this) {
        MATH -> "Math Challenge"
        MEMORY -> "Memory Pattern"
        TYPING -> "Type Phrase"
        SHAKE -> "Shake Challenge"
    }
    
    fun description(): String = when (this) {
        MATH -> "Solve equations to dismiss"
        MEMORY -> "Repeat the pattern shown"
        TYPING -> "Type the exact phrase"
        SHAKE -> "Shake your phone vigorously"
    }
}

enum class MissionDifficulty {
    EASY,
    MEDIUM,
    HARD;
    
    fun displayName(): String = when (this) {
        EASY -> "Easy"
        MEDIUM -> "Medium"
        HARD -> "Hard"
    }
}

data class WakeHistory(
    val id: String = UUID.randomUUID().toString(),
    val alarmId: String,
    val alarmTime: LocalDateTime,
    val wakeTime: LocalDateTime? = null,
    val snoozeCount: Int = 0,
    val missionCompleted: Boolean = false,
    val success: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class StreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalWakeUps: Int = 0,
    val failedWakeUps: Int = 0,
    val lastWakeDate: Long? = null,
    val weeklySuccess: List<Boolean> = List(7) { false }
)

data class UserStats(
    val streakInfo: StreakInfo = StreakInfo(),
    val averageSnoozes: Float = 0f,
    val successRate: Float = 0f,
    val totalMissionsCompleted: Int = 0
)
