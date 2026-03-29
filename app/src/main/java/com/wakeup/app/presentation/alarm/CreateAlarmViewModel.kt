package com.wakeup.app.presentation.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.usecase.CreateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CreateAlarmViewModel @Inject constructor(
    private val createAlarmUseCase: CreateAlarmUseCase
) : ViewModel() {

    fun createAlarm(
        hour: Int,
        minute: Int,
        label: String,
        repeatDays: List<DayOfWeek>,
        missionType: MissionType,
        missionDifficulty: MissionDifficulty,
        strictMode: Boolean,
        useVibration: Boolean,
        gradualVolume: Boolean,
        snoozeEnabled: Boolean
    ) {
        viewModelScope.launch {
            createAlarmUseCase(
                hour = hour,
                minute = minute,
                label = label,
                repeatDays = repeatDays,
                soundUri = null,
                useVibration = useVibration,
                gradualVolume = gradualVolume,
                missionType = missionType,
                missionDifficulty = missionDifficulty,
                strictMode = strictMode,
                snoozeEnabled = snoozeEnabled,
                snoozeInterval = 5,
                maxSnoozes = 3
            )
        }
    }
}
