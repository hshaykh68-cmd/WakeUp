package com.wakeup.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wakeup.app.data.local.dao.AlarmDao
import com.wakeup.app.data.local.dao.WakeHistoryDao
import com.wakeup.app.data.local.entity.AlarmEntity
import com.wakeup.app.data.local.entity.WakeHistoryEntity

@Database(
    entities = [AlarmEntity::class, WakeHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WakeUpDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun wakeHistoryDao(): WakeHistoryDao

    companion object {
        const val DATABASE_NAME = "wakeup_database"
    }
}
