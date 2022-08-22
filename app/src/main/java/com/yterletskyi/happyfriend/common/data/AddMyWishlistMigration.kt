package com.yterletskyi.happyfriend.common.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yterletskyi.happyfriend.features.friends.data.GlobalFriends

class AddMyWishlistMigration : Migration(1, 2) {

    private val myWishlistFriend = GlobalFriends.MyWishlistFriend

    override fun migrate(database: SupportSQLiteDatabase) {
        val contentValues = ContentValues().apply {
            put("id", myWishlistFriend.id)
            put("contact_id", myWishlistFriend.contactId)
            put("position", myWishlistFriend.position)
        }
        database.insert("friend", SQLiteDatabase.CONFLICT_ABORT, contentValues)
    }
}
