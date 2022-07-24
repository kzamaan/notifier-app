package com.softxilla.notification_forwarder.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime

const val DATABASE_NAME = "notifications"
const val TABLE_NAME = "text_messages"

class MessageDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    companion object {
        const val ID = "id"
        const val ANDROID_TITLE = "android_title"
        const val ANDROID_TEXT = "android_text"
        const val CREATED_AT = "created_at"
        const val STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql =
            ("CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $ANDROID_TITLE TEXT, $ANDROID_TEXT TEXT, $CREATED_AT TEXT, $STATUS INTEGER default 0)")
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(sql)
        onCreate(db)
    }

    /**
     * Inserts a new message into the database.
     * @param androidTitle The title of the message.
     * @param androidText The text of the message.
     * @return last added row id.
     */
    fun saveMsgSQLite(
        androidTitle: String,
        androidText: String
    ): Int {
        var rowId = 0
        val db = writableDatabase
        val values = ContentValues()
        values.put(ANDROID_TITLE, androidTitle)
        values.put(ANDROID_TEXT, androidText)
        values.put(CREATED_AT, LocalDateTime.now().toString())
        db.insert(TABLE_NAME, null, values)
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $ID DESC LIMIT 1", null)
        if (cursor.moveToLast()) {
            rowId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return rowId;
    }

    /**
     * Get all messages from the database
     */
    fun getUnSyncedMessage(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $STATUS = 0", null)
    }

    /**
     * Update the status of the message to 1 (synced)
     */
    fun updateMessageStatus(id: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(STATUS, 1)
        db.update(TABLE_NAME, values, "$ID = $id", null)
        db.close()
    }

    /**
     * delete message from database
     */
    fun deleteMessage(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$ID = $id", null)
        db.close()
    }
}