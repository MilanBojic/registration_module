package com.registrationmodule

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.registrationmodule.GlobalConst.DATABASE_NAME
import com.registrationmodule.GlobalConst.USER_TABLE

class UserDataBase : SQLiteOpenHelper {

       private val create_table_query = "create table " + GlobalConst.USER_TABLE + " (" +
       GlobalConst.COL_1 + " integer primary key autoincrement," +
       GlobalConst.COL_2 + " text," +
       GlobalConst.COL_3 + " text" +
       ");"

    constructor(context: Context?) : super(
        context,
        GlobalConst.DATABASE_NAME,
        null,
        1
    ) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        DATABASE_NAME,
        null,
        1
    ) {
    }

    constructor(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int,
        errorHandler: DatabaseErrorHandler?
    ) : super(context, name, factory, version, errorHandler)


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(create_table_query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS" + USER_TABLE)
        onCreate(db)
    }


    fun listOfAllUser(): ArrayList<User> {
        val list = ArrayList<User>()
        val query = "SELECT * FROM " + GlobalConst.USER_TABLE
        val sqlDataBase = this.writableDatabase
        val cursor = sqlDataBase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val user = User()
                user.setId(cursor.getInt(0))
                user.setUserName(cursor.getString(1))
                user.setPassword(cursor.getString(2))
                list.add(user)
            } while (cursor.moveToNext())
        }
        return list

    }

}
