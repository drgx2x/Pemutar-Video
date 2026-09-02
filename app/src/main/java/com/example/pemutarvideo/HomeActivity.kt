package com.example.pemutarvideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnTwitchHome = findViewById<Button>(R.id.btnTwitchHome)
        val btnYoutubeHome = findViewById<Button>(R.id.btnYoutubeHome)

        btnTwitchHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("platform", "twitch")
            startActivity(intent)
        }

        btnYoutubeHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("platform", "youtube")
            startActivity(intent)
        }
    }
}
