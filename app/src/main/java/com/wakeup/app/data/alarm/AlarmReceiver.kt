package com.wakeup.app.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_ALARM_LABEL = "alarm_label"
        const val EXTRA_ALARM_SOUND_URI = "alarm_sound_uri"
        const val EXTRA_ALARM_VIBRATION = "alarm_vibration"
        const val EXTRA_ALARM_GRADUAL_VOLUME = "alarm_gradual_volume"
        const val EXTRA_MISSION_TYPE = "mission_type"
        const val EXTRA_MISSION_DIFFICULTY = "mission_difficulty"
        const val EXTRA_STRICT_MODE = "strict_mode"
        const val EXTRA_SNOOZE_ENABLED = "snooze_enabled"
        const val EXTRA_SNOOZE_INTERVAL = "snooze_interval"
        const val EXTRA_MAX_SNOOZES = "max_snoozes"
        const val EXTRA_IS_SNOOZE = "is_snooze"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, intent.getStringExtra(EXTRA_ALARM_ID))
            putExtra(EXTRA_ALARM_LABEL, intent.getStringExtra(EXTRA_ALARM_LABEL))
            putExtra(EXTRA_ALARM_SOUND_URI, intent.getStringExtra(EXTRA_ALARM_SOUND_URI))
            putExtra(EXTRA_ALARM_VIBRATION, intent.getBooleanExtra(EXTRA_ALARM_VIBRATION, true))
            putExtra(EXTRA_ALARM_GRADUAL_VOLUME, intent.getBooleanExtra(EXTRA_ALARM_GRADUAL_VOLUME, true))
            putExtra(EXTRA_MISSION_TYPE, intent.getStringExtra(EXTRA_MISSION_TYPE) ?: MissionType.MATH.name)
            putExtra(EXTRA_MISSION_DIFFICULTY, intent.getStringExtra(EXTRA_MISSION_DIFFICULTY) ?: MissionDifficulty.EASY.name)
            putExtra(EXTRA_STRICT_MODE, intent.getBooleanExtra(EXTRA_STRICT_MODE, false))
            putExtra(EXTRA_SNOOZE_ENABLED, intent.getBooleanExtra(EXTRA_SNOOZE_ENABLED, true))
            putExtra(EXTRA_SNOOZE_INTERVAL, intent.getIntExtra(EXTRA_SNOOZE_INTERVAL, 5))
            putExtra(EXTRA_MAX_SNOOZES, intent.getIntExtra(EXTRA_MAX_SNOOZES, 3))
            putExtra(EXTRA_IS_SNOOZE, intent.getBooleanExtra(EXTRA_IS_SNOOZE, false))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
