package com.example.pemutarvideo

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnYoutube: Button
    private lateinit var btnTwitch: Button
    private lateinit var btnLiveMode: Button
    private lateinit var btnBrowserMode: Button
    
    private var isLiveMode = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        btnYoutube = findViewById(R.id.btnYoutube)
        btnTwitch = findViewById(R.id.btnTwitch)
        btnLiveMode = findViewById(R.id.btnLiveMode)
        btnBrowserMode = findViewById(R.id.btnBrowserMode)

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
            loadContent("youtube")
        }

        btnTwitch.setOnClickListener {
            loadContent("twitch")
        }

        btnBrowserMode.setOnClickListener {
            isLiveMode = false
            updateModeButtons()
            loadContent(getCurrentPlatform())
        }

        btnLiveMode.setOnClickListener {
            isLiveMode = true
            updateModeButtons()
            loadContent(getCurrentPlatform())
        }

        // Default: YouTube Browser Mode
        updateModeButtons()
        loadContent("youtube")
    }

    private var currentPlatform = "youtube"

    private fun getCurrentPlatform(): String {
        return currentPlatform
    }

    private fun loadContent(platform: String) {
        currentPlatform = platform
        if (platform == "twitch") {
            btnTwitch.alpha = 1.0f
            btnYoutube.alpha = 0.6f
            if (isLiveMode) {
                loadTwitchLiveEmbed()
            } else {
                webView.loadUrl("https://www.twitch.tv")
            }
        } else {
            btnYoutube.alpha = 1.0f
            btnTwitch.alpha = 0.6f
            if (isLiveMode) {
                loadYoutubeLiveEmbed()
            } else {
                webView.loadUrl("https://www.youtube.com")
            }
        }
    }

    private fun updateModeButtons() {
        if (isLiveMode) {
            btnLiveMode.alpha = 1.0f
            btnBrowserMode.alpha = 0.6f
        } else {
            btnBrowserMode.alpha = 1.0f
            btnLiveMode.alpha = 0.6f
        }
    }

    private fun loadYoutubeLiveEmbed() {
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

    private fun loadTwitchLiveEmbed() {
        val twitchHtml = """
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
                    <iframe src="https://player.twitch.tv/?channel=monstercat&parent=localhost&parent=twitch.tv" allowfullscreen></iframe>
                </div>
                <div id="chat">
                    <iframe src="https://www.twitch.tv/embed/monstercat/chat?parent=localhost&parent=twitch.tv" height="100%" width="100%"></iframe>
                </div>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://www.twitch.tv", twitchHtml, "text/html", "UTF-8", null)
    }

    override fun onBackPressed() {
        if (!isLiveMode && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
