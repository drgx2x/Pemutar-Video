package com.example.pemutarvideo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnMenu: Button
    private var sleepTimer: CountDownTimer? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        btnMenu = findViewById(R.id.btnMenu)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.loadsImagesAutomatically = true

        webView.webViewClient = WebViewClient()
        
        webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalSystemUiVisibility = 0
            private var originalOrientation = 0

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                originalSystemUiVisibility = window.decorView.systemUiVisibility
                originalOrientation = requestedOrientation

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

                webView.visibility = View.GONE
                val decorView = window.decorView as FrameLayout
                decorView.addView(customView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ))
                customViewCallback = callback
            }

            override fun onHideCustomView() {
                val decorView = window.decorView as FrameLayout
                decorView.removeView(customView)
                customView = null

                window.decorView.systemUiVisibility = originalSystemUiVisibility
                requestedOrientation = originalOrientation

                webView.visibility = View.VISIBLE
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
            }
        }

        btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Default buka Beranda YouTube
        loadYoutubeHome()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "YouTube")
        popup.menu.add(0, 2, 1, "Twitch")
        popup.menu.add(0, 3, 2, "Pop-up Live Chat YouTube")
        popup.menu.add(0, 4, 3, "Sleep Timer (15 Min)")
        popup.menu.add(0, 5, 4, "Sleep Timer (30 Min)")
        popup.menu.add(0, 6, 5, "Sleep Timer (60 Min)")
        popup.menu.add(0, 7, 6, "Sleep Timer (Manual)")
        popup.menu.add(0, 8, 7, "Cancel Sleep Timer")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    loadYoutubeHome()
                    true
                }
                2 -> {
                    loadTwitchHome()
                    true
                }
                3 -> {
                    showYoutubeLiveChatDialog()
                    true
                }
                4 -> {
                    startSleepTimer(15 * 60 * 1000L, "15 Menit")
                    true
                }
                5 -> {
                    startSleepTimer(30 * 60 * 1000L, "30 Menit")
                    true
                }
                6 -> {
                    startSleepTimer(60 * 60 * 1000L, "60 Menit")
                    true
                }
                7 -> {
                    showManualSleepDialog()
                    true
                }
                8 -> {
                    cancelSleepTimer()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showYoutubeLiveChatDialog() {
        val input = EditText(this)
        input.hint = "Contoh: dQw4w9WgXcQ atau URL lengkap video"

        AlertDialog.Builder(this)
            .setTitle("Pop-up Live Chat YouTube")
            .setMessage("Masukkan Video ID atau URL video YouTube Live:")
            .setView(input)
            .setPositiveButton("Buka Chat") { _, _ =>
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    val videoId = extractYouTubeId(text)
                    if (videoId != null) {
                        // URL Pop-up Live Chat resmi YouTube
                        val chatUrl = "https://www.youtube.com/live_chat?v=$videoId&embed_domain=${webView.url?.let { android.net.Uri.parse(it).host } ?: "youtube.com"}"
                        // Alternatif sederhana: webView.loadUrl("https://www.youtube.com/live_chat?v=$videoId")
                        webView.loadUrl("https://www.youtube.com/live_chat?v=$videoId")
                    } else {
                        Toast.makeText(this, "Video ID tidak valid!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Input tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun extractYouTubeId(urlOrId: String): String? {
        if (urlOrId.length == 11 && !urlOrId.contains("/") && !urlOrId.contains(".")) {
            return urlOrId
        }
        // Pola sederhana untuk URL YouTube
        return try {
            val uri = android.net.Uri.parse(urlOrId)
            when {
                uri.host?.contains("youtu.be") == true -> uri.lastPathSegment
                uri.host?.contains("youtube.com") == true -> {
                    uri.getQueryParameter("v") ?: uri.lastPathSegment
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun showManualSleepDialog() {
        val input = EditText(this)
        input.hint = "Masukkan waktu dalam menit"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Sleep Timer Manual")
            .setMessage("Masukkan durasi tidur dalam menit:")
            .setView(input)
            .setPositiveButton("Mulai") { _, _ =>
                val valueStr = input.text.toString()
                if (valueStr.isNotEmpty()) {
                    val minutes = valueStr.toLongOrNull()
                    if (minutes != null && minutes > 0) {
                        startSleepTimer(minutes * 60 * 1000L, "$minutes Menit")
                    } else {
                        Toast.makeText(this, "Masukkan angka yang valid!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Input tidak boleh kosong!", Toast.LENGTH_SHORT).is
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun startSleepTimer(durationMillis: Long, label: String) {
        sleepTimer?.cancel()
        Toast.makeText(this, "Sleep timer diatur untuk $label", Toast.LENGTH_SHORT).show()
        
        sleepTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                Toast.makeText(this@MainActivity, "Waktu tidur tercapai, menutup aplikasi...", Toast.LENGTH_LONG).show()
                finish()
            }
        }.start()
    }

    private fun cancelSleepTimer() {
        sleepTimer?.cancel()
        sleepTimer = null
        Toast.makeText(this, "Sleep timer dibatalkan", Toast.LENGTH_SHORT).show()
    }

    private fun loadYoutubeHome() {
        webView.loadUrl("https://www.youtube.com")
    }

    private fun loadTwitchHome() {
        webView.loadUrl("https://www.twitch.tv")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
