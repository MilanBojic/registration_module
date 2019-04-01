package com.registrationmodule

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mGroupBox: RadioGroup
    private lateinit var mRegisterButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        initViews()
        mRegisterButton.setOnClickListener {
            storeToDataBase()
        }

    }

    private lateinit var sqlLiteDataBase: UserDataBase

    private fun initViews() {
        mUserName = findViewById(R.id.register_username_field)
        mPassword = findViewById(R.id.register_password_field)
        mRegisterButton = findViewById(R.id.register_button)
        mGroupBox = findViewById(R.id.group_box_id)
        sqlLiteDataBase = UserDataBase(this)
    }

    private fun storeToDataBase() {
        val userName = mUserName.text.toString()
        val password = mPassword.text.toString()
        if (userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.userEmpty), Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show()
            return
        }


        val radioButton = findViewById(mGroupBox.checkedRadioButtonId) as RadioButton

        if (isValidationOk(userName, radioButton.text.toString())) {
            store(userName, password, radioButton.text.toString())
        }

    }

    private fun isValidationOk(userName: String, index: String): Boolean {
        var list = ArrayList<User>()
        when (index) {
            GlobalConst.SQLITE -> list = sqlLiteDataBase.listOfAllUser()
            GlobalConst.STORIO -> {
            }
            GlobalConst.ROOM -> {
            }
        }

        list.forEach { t: User? ->
            run {
                if (t!!.userName.equals(userName)) {
                    Toast.makeText(
                        this,
                        getString(R.string.tDifferentUser),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            }
        }
        return true
    }

    private fun store(userName: String, password: String, index: String) {
        when (index) {
            GlobalConst.SQLITE -> {
                val contentValues = ContentValues()
                contentValues.put(GlobalConst.COL_2, userName)
                contentValues.put(GlobalConst.COL_3, password)
                sqlLiteDataBase.writableDatabase.insert(GlobalConst.USER_TABLE, null, contentValues)
            }
            GlobalConst.STORIO -> {
            }
            GlobalConst.ROOM -> {
            }
        }

    }
}
