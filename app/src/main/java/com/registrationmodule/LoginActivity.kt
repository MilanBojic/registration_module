package com.registrationmodule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        initViews()

        mLoginButton.setOnClickListener {
            if (isValidationOk()) {
                login()
            }
        }
    }

    private fun isValidationOk(): Boolean {
        val userName = mUserName.text.toString()
        val password = mPassword.text.toString()
        if (userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.userEmpty), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun initViews() {
        mUserName = this.findViewById(R.id.login_username_field)
        mPassword = this.findViewById(R.id.login_password_field)
        mLoginButton = this.findViewById(R.id.login_button)
    }

    fun login() {
        Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show()
    }


}
