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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        btnYoutube = findViewById(R.id.btnYoutube)
        btnTwitch = findViewById(R.id.btnTwitch)

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

                // Otomatis landscape saat tombol fullscreen pemutar diklik
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

        // Tombol switch platform di atas
        btnYoutube.setOnClickListener {
            loadPlatform("youtube")
        }

        btnTwitch.setOnClickListener {
            loadPlatform("twitch")
        }

        // Default buka halaman utama YouTube (bukan pemutar langsung)
        loadPlatform("youtube")
    }

    private fun loadPlatform(platform: String) {
        if (platform == "twitch") {
            btnTwitch.alpha = 1.0f
            btnYoutube.alpha = 0.6f
            webView.loadUrl("https://www.twitch.tv")
        } else {
            btnYoutube.alpha = 1.0f
            btnTwitch.alpha = 0.6f
            webView.loadUrl("https://www.youtube.com")
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
