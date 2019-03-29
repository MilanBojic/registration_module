package com.registrationmodule

import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mRegisterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        initViews()
        mRegisterButton.setOnClickListener {
            storeToDataBase()
        }

    }

    private fun initViews() {
        mUserName = findViewById(R.id.register_username_field)
        mPassword = findViewById(R.id.register_password_field)
        mRegisterButton = findViewById(R.id.register_button)
    }

    private fun storeToDataBase() {
        val string = mUserName.text.toString() + mPassword.text.toString()
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
}
