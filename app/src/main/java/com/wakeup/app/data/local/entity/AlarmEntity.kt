package com.wakeup.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import java.time.DayOfWeek

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey
    val id: String,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: List<DayOfWeek>,
    val soundUri: String?,
    val useVibration: Boolean,
    val gradualVolume: Boolean,
    val missionType: MissionType,
    val missionDifficulty: MissionDifficulty,
    val strictMode: Boolean,
    val snoozeEnabled: Boolean,
    val snoozeInterval: Int,
    val maxSnoozes: Int,
    val createdAt: Long
) {
    fun toDomainModel(): Alarm {
        return Alarm(
            id = id,
            hour = hour,
            minute = minute,
            label = label,
            isEnabled = isEnabled,
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
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomainModel(alarm: Alarm): AlarmEntity {
            return AlarmEntity(
                id = alarm.id,
                hour = alarm.hour,
                minute = alarm.minute,
                label = alarm.label,
                isEnabled = alarm.isEnabled,
                repeatDays = alarm.repeatDays,
                soundUri = alarm.soundUri,
                useVibration = alarm.useVibration,
                gradualVolume = alarm.gradualVolume,
                missionType = alarm.missionType,
                missionDifficulty = alarm.missionDifficulty,
                strictMode = alarm.strictMode,
                snoozeEnabled = alarm.snoozeEnabled,
                snoozeInterval = alarm.snoozeInterval,
                maxSnoozes = alarm.maxSnoozes,
                createdAt = alarm.createdAt
            )
        }
    }
}
