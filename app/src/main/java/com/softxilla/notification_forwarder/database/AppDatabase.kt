package com.softxilla.notification_forwarder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.softxilla.notification_forwarder.database.dao.AppDao
import com.softxilla.notification_forwarder.database.entities.AppList


@Database(
    entities = [
        AppList::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}