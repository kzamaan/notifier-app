package me.kzaman.notification_forwarder.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.kzaman.notification_forwarder.database.entities.AppList


@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApp(product: ArrayList<AppList>): LongArray

    @Query("SELECT * FROM app_list")
    fun getAppList(): List<AppList>

    @Query("DELETE FROM app_list")
    fun deleteAppTable()
}