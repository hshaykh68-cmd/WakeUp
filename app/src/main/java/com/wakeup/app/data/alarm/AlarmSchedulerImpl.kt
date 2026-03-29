package com.wakeup.app.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wakeup.app.domain.model.Alarm
import com.wakeup.app.domain.usecase.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm) {
        val nextRingTime = alarm.getNextRingTime()
        val triggerAtMillis = nextRingTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_ALARM_SOUND_URI, alarm.soundUri)
            putExtra(AlarmReceiver.EXTRA_ALARM_VIBRATION, alarm.useVibration)
            putExtra(AlarmReceiver.EXTRA_ALARM_GRADUAL_VOLUME, alarm.gradualVolume)
            putExtra(AlarmReceiver.EXTRA_MISSION_TYPE, alarm.missionType.name)
            putExtra(AlarmReceiver.EXTRA_MISSION_DIFFICULTY, alarm.missionDifficulty.name)
            putExtra(AlarmReceiver.EXTRA_STRICT_MODE, alarm.strictMode)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, alarm.snoozeEnabled)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_INTERVAL, alarm.snoozeInterval)
            putExtra(AlarmReceiver.EXTRA_MAX_SNOOZES, alarm.maxSnoozes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(alarmId: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun snooze(alarm: Alarm, snoozeMinutes: Int) {
        val triggerAtMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_ALARM_SOUND_URI, alarm.soundUri)
            putExtra(AlarmReceiver.EXTRA_ALARM_VIBRATION, alarm.useVibration)
            putExtra(AlarmReceiver.EXTRA_ALARM_GRADUAL_VOLUME, alarm.gradualVolume)
            putExtra(AlarmReceiver.EXTRA_MISSION_TYPE, alarm.missionType.name)
            putExtra(AlarmReceiver.EXTRA_MISSION_DIFFICULTY, alarm.missionDifficulty.name)
            putExtra(AlarmReceiver.EXTRA_STRICT_MODE, alarm.strictMode)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, alarm.snoozeEnabled)
            putExtra(AlarmReceiver.EXTRA_IS_SNOOZE, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode() + 10000, // Different request code for snooze
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}
