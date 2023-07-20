package com.example.swipe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Запускаем активити приветствия (WelcomeActivity)
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)

        // Завершаем MainActivity
        finish()
    }
}
