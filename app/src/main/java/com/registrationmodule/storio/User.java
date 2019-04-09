package com.registrationmodule.storio;

import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType;

import java.io.Serializable;


@StorIOSQLiteType(table = "User", generateTableClass = false)
public class User implements Serializable {

    @StorIOSQLiteColumn(name = "_id", key = true)
    Long id;

    @StorIOSQLiteColumn(name = "username")
    String userName;

    @StorIOSQLiteColumn(name = "password")
    String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
