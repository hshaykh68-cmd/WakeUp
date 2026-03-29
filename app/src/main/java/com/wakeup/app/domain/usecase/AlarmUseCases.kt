package com.wakeup.app.domain.usecase

import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.repository.AlarmRepository
import java.time.DayOfWeek
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase
) {
    suspend operator fun invoke(
        hour: Int,
        minute: Int,
        label: String = "Alarm",
        repeatDays: List<DayOfWeek> = emptyList(),
        soundUri: String? = null,
        useVibration: Boolean = true,
        gradualVolume: Boolean = true,
        missionType: MissionType = MissionType.MATH,
        missionDifficulty: MissionDifficulty = MissionDifficulty.EASY,
        strictMode: Boolean = false,
        snoozeEnabled: Boolean = true,
        snoozeInterval: Int = 5,
        maxSnoozes: Int = 3
    ): Result<Alarm> {
        return try {
            val alarm = alarmRepository.createAlarm(
                hour = hour,
                minute = minute,
                label = label,
                repeatDays = repeatDays,
                soundUri = soundUri,
                useVibration = useVibration,
                gradualVolume = gradualVolume,
                missionType = missionType,
                missionDifficulty = missionDifficulty,
                strictMode = strictMode,
                snoozeEnabled = snoozeEnabled,
                snoozeInterval = snoozeInterval,
                maxSnoozes = maxSnoozes
            )
            scheduleAlarmUseCase(alarm)
            Result.success(alarm)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        return try {
            alarmRepository.updateAlarm(alarm)
            if (alarm.isEnabled) {
                scheduleAlarmUseCase(alarm)
            } else {
                cancelAlarmUseCase(alarm.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val cancelAlarmUseCase: CancelAlarmUseCase
) {
    suspend operator fun invoke(alarmId: String): Result<Unit> {
        return try {
            cancelAlarmUseCase(alarmId)
            alarmRepository.deleteAlarm(alarmId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ToggleAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase
) {
    suspend operator fun invoke(alarm: Alarm): Result<Unit> {
        return try {
            val newState = !alarm.isEnabled
            alarmRepository.toggleAlarm(alarm.id, newState)
            if (newState) {
                scheduleAlarmUseCase(alarm.copy(isEnabled = true))
            } else {
                cancelAlarmUseCase(alarm.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DuplicateAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase
) {
    suspend operator fun invoke(alarmId: String): Result<Alarm?> {
        return try {
            val newAlarm = alarmRepository.duplicateAlarm(alarmId)
            newAlarm?.let { scheduleAlarmUseCase(it) }
            Result.success(newAlarm)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
