package com.registrationmodule.storio

import android.content.Context
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping
import com.pushtorefresh.storio3.sqlite.queries.Query
import com.registrationmodule.GlobalConst
import com.registrationmodule.SQLiteDB
import java.util.*

class StorioApi(context: Context) {


    private var storioDB: StorioWrapperDB

    init {
        val sqlLite = SQLiteDB(context)
        val mapping = SQLiteTypeMapping.builder<User>()
            .putResolver(UserStorIOSQLitePutResolver())
            .getResolver(UserStorIOSQLiteGetResolver())
            .deleteResolver(UserStorIOSQLiteDeleteResolver())
            .build()

        storioDB = StorioWrapperDB.builder()
            .sqliteOpenHelper(sqlLite)
            .addTypeMapping(User::class.java, mapping)
            .build()
    }

    fun addUser(user: User) {
        storioDB.put().`object`(user).prepare().executeAsBlocking()
    }

    fun getAllUsers(): ArrayList<User> {
        val list = storioDB.get().listOfObjects(User::class.java)
            .withQuery(Query.builder().table(GlobalConst.USER_TABLE)
                .build())
            .prepare()
            .executeAsBlocking()
        val arrayList  = ArrayList<User>()
        arrayList.addAll(list!!)
        return arrayList
    }

}

