package com.wakeup.app.domain.usecase

import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun isOnboardingCompleted(): Boolean {
        return settingsRepository.isOnboardingCompleted()
    }

    suspend fun getDefaultMissionType(): MissionType {
        return settingsRepository.getDefaultMissionType()
    }

    suspend fun getDefaultDifficulty(): MissionDifficulty {
        return settingsRepository.getDefaultDifficulty()
    }

    suspend fun isPremiumUser(): Boolean {
        return settingsRepository.isPremiumUser()
    }

    suspend fun getUse24HourFormat(): Boolean {
        return settingsRepository.getUse24HourFormat()
    }
}

class SaveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsRepository.setOnboardingCompleted(completed)
    }

    suspend fun setDefaultMissionType(type: MissionType) {
        settingsRepository.setDefaultMissionType(type)
    }

    suspend fun setDefaultDifficulty(difficulty: MissionDifficulty) {
        settingsRepository.setDefaultDifficulty(difficulty)
    }

    suspend fun setPremiumUser(isPremium: Boolean) {
        settingsRepository.setPremiumUser(isPremium)
    }

    suspend fun setUse24HourFormat(use24Hour: Boolean) {
        settingsRepository.setUse24HourFormat(use24Hour)
    }
}
