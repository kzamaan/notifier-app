package me.kzaman.notification_forwarder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.kzaman.notification_forwarder.database.AppDatabase
import me.kzaman.notification_forwarder.database.dao.AppDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "android_mvvm")
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideAppDAO(database: AppDatabase): AppDao = database.appDao()
}