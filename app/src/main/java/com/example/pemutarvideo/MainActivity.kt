package com.example.pemutarvideo

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnYoutube: Button
    private lateinit var btnTwitch: Button
    private lateinit var btnMenu: Button
    private var currentPlatform = "youtube"
    private var sleepTimer: CountDownTimer? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        btnYoutube = findViewById(R.id.btnYoutube)
        btnTwitch = findViewById(R.id.btnTwitch)
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

        btnYoutube.setOnClickListener {
            loadYoutubeLive()
        }

        btnTwitch.setOnClickListener {
            loadTwitchLive()
        }

        btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Default buka YouTube Live Stream + Chat
        loadYoutubeLive()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "YouTube Live")
        popup.menu.add(0, 2, 1, "Twitch Live")
        popup.menu.add(0, 3, 2, "Sleep Timer (15 Min)")
        popup.menu.add(0, 4, 3, "Sleep Timer (30 Min)")
        popup.menu.add(0, 5, 4, "Sleep Timer (60 Min)")
        popup.menu.add(0, 6, 5, "Cancel Sleep Timer")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    loadYoutubeLive()
                    true
                }
                2 -> {
                    loadTwitchLive()
                    true
                }
                3 -> {
                    startSleepTimer(15 * 60 * 1000L, "15 Menit")
                    true
                }
                4 -> {
                    startSleepTimer(30 * 60 * 1000L, "30 Menit")
                    true
                }
                5 -> {
                    startSleepTimer(60 * 60 * 1000L, "60 Menit")
                    true
                }
                6 -> {
                    cancelSleepTimer()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun startSleepTimer(durationMillis: Long, label: String) {
        sleepTimer?.cancel()
        Toast.makeText(this, "Sleep timer diatur untuk $label", Toast.LENGTH_SHORT).show()
        
        sleepTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Bisa tambahkan logic update jika perlu
            }

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

    private fun loadYoutubeLive() {
        currentPlatform = "youtube"
        btnYoutube.alpha = 1.0f
        btnTwitch.alpha = 0.6f
        
        val youtubeHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; display: flex; flex-direction: column; }
                    #video-container { width: 100%; height: 60%; }
                    #chat-container { width: 100%; height: 40%; background: #212121; }
                    iframe { width: 100%; height: 100%; border: none; }
                </style>
            </head>
            <body>
                <div id="video-container">
                    <iframe src="https://www.youtube.com/embed/live_stream?channel=UCBJycsmduvYEL83R_U4JriQ&autoplay=1" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                </div>
                <div id="chat-container">
                    <iframe src="https://www.youtube.com/live_chat?v=live_stream&embed_domain=localhost"></iframe>
                </div>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://www.youtube.com", youtubeHtml, "text/html", "UTF-8", null)
    }

    private fun loadTwitchLive() {
        currentPlatform = "twitch"
        btnTwitch.alpha = 1.0f
        btnYoutube.alpha = 0.6f

        val twitchHtml = """
            <!DOCTYPE html>
            # stream { width: 100%; height: 60%; }
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; display: flex; flex-direction: column; }
                    #stream { width: 100%; height: 60%; }
                    #chat { width: 100%; height: 40%; background: #18181b; }
                    iframe { width: 100%; height: 100%; border: none; }
                </style>
            </head>
            <body>
                <div id="stream">
                    <iframe src="https://player.twitch.tv/?channel=monstercat&parent=127.0.0.1" allowfullscreen></iframe>
                </div>
                <div id="chat">
                    <iframe src="https://www.twitch.tv/embed/monstercat/chat?parent=127.0.0.1" height="100%" width="100%"></iframe>
                </div>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("http://127.0.0.1", twitchHtml, "text/html", "UTF-8", null)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
