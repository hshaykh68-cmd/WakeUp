package com.wakeup.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import com.wakeup.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DEFAULT_MISSION_TYPE = stringPreferencesKey("default_mission_type")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
        val IS_PREMIUM_USER = booleanPreferencesKey("is_premium_user")
        val USE_24_HOUR_FORMAT = booleanPreferencesKey("use_24_hour_format")
    }

    override suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.map { it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false }.first()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    override suspend fun getDefaultMissionType(): MissionType {
        val typeString = dataStore.data.map { it[PreferencesKeys.DEFAULT_MISSION_TYPE] }.first()
        return typeString?.let { MissionType.valueOf(it) } ?: MissionType.MATH
    }

    override suspend fun setDefaultMissionType(type: MissionType) {
        dataStore.edit { it[PreferencesKeys.DEFAULT_MISSION_TYPE] = type.name }
    }

    override suspend fun getDefaultDifficulty(): MissionDifficulty {
        val difficultyString = dataStore.data.map { it[PreferencesKeys.DEFAULT_DIFFICULTY] }.first()
        return difficultyString?.let { MissionDifficulty.valueOf(it) } ?: MissionDifficulty.EASY
    }

    override suspend fun setDefaultDifficulty(difficulty: MissionDifficulty) {
        dataStore.edit { it[PreferencesKeys.DEFAULT_DIFFICULTY] = difficulty.name }
    }

    override suspend fun isPremiumUser(): Boolean {
        return dataStore.data.map { it[PreferencesKeys.IS_PREMIUM_USER] ?: false }.first()
    }

    override suspend fun setPremiumUser(isPremium: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_PREMIUM_USER] = isPremium }
    }

    override suspend fun getUse24HourFormat(): Boolean {
        return dataStore.data.map { it[PreferencesKeys.USE_24_HOUR_FORMAT] ?: false }.first()
    }

    override suspend fun setUse24HourFormat(use24Hour: Boolean) {
        dataStore.edit { it[PreferencesKeys.USE_24_HOUR_FORMAT] = use24Hour }
    }

    fun getPremiumUserFlow(): Flow<Boolean> {
        return dataStore.data.map { it[PreferencesKeys.IS_PREMIUM_USER] ?: false }
    }
}
