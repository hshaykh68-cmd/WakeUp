package com.wakeup.app.domain.usecase

import com.wakeup.app.domain.model.Alarm
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm) {
        alarmScheduler.schedule(alarm)
    }
}

class CancelAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarmId: String) {
        alarmScheduler.cancel(alarmId)
    }
}

class RescheduleAllAlarmsUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val getEnabledAlarmsUseCase: GetEnabledAlarmsUseCase
) {
    suspend operator fun invoke() {
        val alarms = getEnabledAlarmsUseCase()
        alarms.forEach { alarmScheduler.schedule(it) }
    }
}

class SnoozeAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm, snoozeMinutes: Int = 5) {
        alarmScheduler.snooze(alarm, snoozeMinutes)
    }
}

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarmId: String)
    fun snooze(alarm: Alarm, snoozeMinutes: Int)
}
