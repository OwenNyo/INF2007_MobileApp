package com.example.inf2007_proj

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.inf2007_proj.Activity.Homepage.Homepage
import com.example.inf2007_proj.Activity.Registration.Registration
import com.example.inf2007_proj.Session.Session
import com.example.inf2007_proj.ViewModel.LoginViewModel

class MainActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var session: Session

    private lateinit var editTextName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        session = Session(applicationContext)

        editTextName = findViewById(R.id.editTextTextPersonName)
        editTextPassword = findViewById(R.id.editTextTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewRegister = findViewById(R.id.textViewRegister)

        if (session.getLogin()) {
            startActivity(Intent(this, Homepage::class.java))
            finish()
        }

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, Registration::class.java))
        }

        buttonLogin.setOnClickListener {
            val username = editTextName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (username.isEmpty()) {
                editTextName.error = "Username cannot be empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            loginViewModel.login(username, password)
        }

        loginViewModel.loginStatus.observe(this) { status ->
            if (status == "Pass") {
                session.setLogin(true)
                session.setUsername(editTextName.text.toString())

                startActivity(Intent(this, Homepage::class.java))
                finish()

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
