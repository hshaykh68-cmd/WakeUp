package com.wakeup.app.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.wakeup.app.data.alarm.AlarmSchedulerImpl
import com.wakeup.app.data.local.WakeUpDatabase
import com.wakeup.app.data.repository.AlarmRepositoryImpl
import com.wakeup.app.data.repository.SettingsRepositoryImpl
import com.wakeup.app.data.repository.StatsRepositoryImpl
import com.wakeup.app.data.repository.WakeHistoryRepositoryImpl
import com.wakeup.app.domain.repository.AlarmRepository
import com.wakeup.app.domain.repository.SettingsRepository
import com.wakeup.app.domain.repository.StatsRepository
import com.wakeup.app.domain.repository.WakeHistoryRepository
import com.wakeup.app.domain.usecase.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WakeUpDatabase {
        return Room.databaseBuilder(
            context,
            WakeUpDatabase::class.java,
            WakeUpDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(database: WakeUpDatabase) = database.alarmDao()

    @Provides
    @Singleton
    fun provideWakeHistoryDao(database: WakeUpDatabase) = database.wakeHistoryDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("wakeup_prefs") }
        )
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(alarmRepositoryImpl: AlarmRepositoryImpl): AlarmRepository {
        return alarmRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository {
        return settingsRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideStatsRepository(statsRepositoryImpl: StatsRepositoryImpl): StatsRepository {
        return statsRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideWakeHistoryRepository(wakeHistoryRepositoryImpl: WakeHistoryRepositoryImpl): WakeHistoryRepository {
        return wakeHistoryRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(alarmSchedulerImpl: AlarmSchedulerImpl): AlarmScheduler {
        return alarmSchedulerImpl
    }
}
