package com.wakeup.app.data.repository

import com.wakeup.app.data.local.dao.AlarmDao
import com.wakeup.app.data.local.entity.AlarmEntity
import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override suspend fun createAlarm(
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
    ): Alarm {
        val alarm = AlarmEntity(
            id = UUID.randomUUID().toString(),
            hour = hour,
            minute = minute,
            label = label,
            isEnabled = true,
            repeatDays = repeatDays,
            soundUri = soundUri,
            useVibration = useVibration,
            gradualVolume = gradualVolume,
            missionType = missionType,
            missionDifficulty = missionDifficulty,
            strictMode = strictMode,
            snoozeEnabled = snoozeEnabled,
            snoozeInterval = snoozeInterval,
            maxSnoozes = maxSnoozes,
            createdAt = System.currentTimeMillis()
        )
        alarmDao.insertAlarm(alarm)
        return alarm.toDomainModel()
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(AlarmEntity.fromDomainModel(alarm))
    }

    override suspend fun deleteAlarm(alarmId: String) {
        alarmDao.deleteAlarmById(alarmId)
    }

    override suspend fun getAlarmById(alarmId: String): Alarm? {
        return alarmDao.getAlarmById(alarmId)?.toDomainModel()
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getEnabledAlarms(): Flow<List<Alarm>> {
        return alarmDao.getEnabledAlarms().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun toggleAlarm(alarmId: String, isEnabled: Boolean) {
        alarmDao.toggleAlarm(alarmId, isEnabled)
    }

    override suspend fun duplicateAlarm(alarmId: String): Alarm? {
        val original = alarmDao.getAlarmById(alarmId) ?: return null
        val duplicate = original.copy(
            id = UUID.randomUUID().toString(),
            label = original.label + " (Copy)",
            isEnabled = false,
            createdAt = System.currentTimeMillis()
        )
        alarmDao.insertAlarm(duplicate)
        return duplicate.toDomainModel()
    }
}
