package me.kzaman.notification_forwarder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.kzaman.notification_forwarder.database.dao.AppDao
import me.kzaman.notification_forwarder.database.entities.AppList


@Database(
    entities = [
        AppList::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}