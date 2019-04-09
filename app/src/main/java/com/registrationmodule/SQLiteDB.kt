package com.registrationmodule

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.registrationmodule.storio.User
import java.util.*

class SQLiteDB : SQLiteOpenHelper {

       private val create_table_query = ("CREATE TABLE IF NOT EXISTS " + GlobalConst.USER_TABLE + "("
               + GlobalConst.COL_1 + " INTEGER PRIMARY KEY,"
               + GlobalConst.COL_2 + " TEXT, "
               + GlobalConst.COL_3 + " TEXT"
               + ")")

    constructor(context: Context?) : super(
        context,
        GlobalConst.DATABASE_NAME,
        null,
        1
    )


    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(create_table_query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }


    fun getAllUsers(): ArrayList<User> {
        val list = ArrayList<User>()
        val query = "SELECT * FROM " + GlobalConst.USER_TABLE
        val sqlDataBase = this.writableDatabase
        val cursor = sqlDataBase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val user = User()
                user.setId(cursor.getLong(0))
                user.setUserName(cursor.getString(1))
                user.setPassword(cursor.getString(2))
                list.add(user)
            } while (cursor.moveToNext())
        }
        return list

    }

}
