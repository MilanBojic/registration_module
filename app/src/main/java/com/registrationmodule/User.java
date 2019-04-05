package com.registrationmodule;

import android.provider.BaseColumns;
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType;

import java.io.Serializable;


@StorIOSQLiteType(table = "User", generateTableClass = false)
public class User implements Serializable {

    @StorIOSQLiteColumn(name = BaseColumns._ID, key = true, version = 0)
    int id;

    @StorIOSQLiteColumn(name = "username")
    String userName;

    @StorIOSQLiteColumn(name = "password")
    String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
