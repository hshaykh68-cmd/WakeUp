package com.wakeup.app.domain.repository

import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.UserStats
import com.wakeup.app.domain.model.WakeHistory
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface AlarmRepository {
    suspend fun createAlarm(
        hour: Int,
        minute: Int,
        label: String,
        repeatDays: List<DayOfWeek>,
        soundUri: String?,
        useVibration: Boolean,
        gradualVolume: Boolean,
        missionType: MissionType,
        missionDifficulty: MissionDifficulty,
        strictMode: Boolean,
        snoozeEnabled: Boolean,
        snoozeInterval: Int,
        maxSnoozes: Int
    ): Alarm

    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarmId: String)
    suspend fun getAlarmById(alarmId: String): Alarm?
    fun getAllAlarms(): Flow<List<Alarm>>
    fun getEnabledAlarms(): Flow<List<Alarm>>
    suspend fun toggleAlarm(alarmId: String, isEnabled: Boolean)
    suspend fun duplicateAlarm(alarmId: String): Alarm?
}

interface WakeHistoryRepository {
    suspend fun recordAlarmTriggered(alarmId: String, alarmTime: Long): WakeHistory
    suspend fun recordWakeAttempt(historyId: String, snoozeCount: Int, missionCompleted: Boolean, success: Boolean)
    suspend fun getHistoryForAlarm(alarmId: String): List<WakeHistory>
    fun getAllHistory(): Flow<List<WakeHistory>>
    fun getRecentHistory(days: Int): Flow<List<WakeHistory>>
}

interface StatsRepository {
    suspend fun getUserStats(): UserStats
    suspend fun updateStreak(success: Boolean)
    suspend fun incrementMissionCount()
    fun getStreakFlow(): Flow<Int>
}

interface SettingsRepository {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun getDefaultMissionType(): MissionType
    suspend fun setDefaultMissionType(type: MissionType)
    suspend fun getDefaultDifficulty(): MissionDifficulty
    suspend fun setDefaultDifficulty(difficulty: MissionDifficulty)
    suspend fun isPremiumUser(): Boolean
    suspend fun setPremiumUser(isPremium: Boolean)
    suspend fun getUse24HourFormat(): Boolean
    suspend fun setUse24HourFormat(use24Hour: Boolean)
}
