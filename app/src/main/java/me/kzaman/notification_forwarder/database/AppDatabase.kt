package me.kzaman.notification_forwarder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.kzaman.notification_forwarder.database.dao.UserDao
import me.kzaman.notification_forwarder.database.entities.UserEntities


@Database(
    entities = [
        UserEntities::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}