package com.example.pemutarvideo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
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
    private var currentUrl: String = ""

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
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    currentUrl = url
                    injectAdBlock(url)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    currentUrl = url
                    injectAdBlock(url)
                }
                return false
            }
        }
        
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
                
                // JANGAN sembunyikan navigasi secara total agar tombol Back/Home sistem atau menu pop-up masih bisa dijangkau jika disentuh/di-swipe,
                // atau gunakan flag sistem standar agar video fullscreen tetap optimal tapi tidak mengunci total interaksi window.
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

                // Sembunyikan topbar dan webView utama, tampilkan customView (fullscreen video) di root layout
                findViewById<View>(R.id.layoutTopBar).visibility = View.GONE
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

                findViewById<View>(R.id.layoutTopBar).visibility = View.VISIBLE
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
        popup.menu.add(0, 3, 2, "Sleep Timer")
        popup.menu.add(0, 4, 3, "Cancel Sleep Timer")

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
                    showSleepTimerOptionsDialog()
                    true
                }
                4 -> {
                    cancelSleepTimer()
                    true
                }
                5 -> {
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showSleepTimerOptionsDialog() {
        val options = arrayOf("15 Menit", "30 Menit", "60 Menit", "Manual (Custom Menit)")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sleep Timer")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startSleepTimer(15 * 60 * 1000L, "15 Menit")
                    1 -> startSleepTimer(30 * 60 * 1000L, "30 Menit")
                    2 -> startSleepTimer(60 * 60 * 1000L, "60 Menit")
                    3 -> showManualSleepTimerDialog()
                }
            }
            .show()
    }

    private fun showManualSleepTimerDialog() {
        val input = EditText(this)
        input.hint = "Masukkan jumlah menit"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Sleep Timer Manual")
            .setMessage("Masukkan durasi dalam menit:")
            .setView(input)
            .setPositiveButton("Mulai") { _, _ ->
                val minutesStr = input.text.toString()
                if (minutesStr.isNotEmpty()) {
                    val minutes = minutesStr.toLongOrNull()
                    if (minutes != null && minutes > 0) {
                        startSleepTimer(minutes * 60 * 1000L, "$minutes Menit")
                    } else {
                        Toast.makeText(this, "Angka tidak valid", Toast.LENGTH_SHORT).show()
                    }
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



    private fun extractVideoId(url: String): String? {
        // Cek URL standar watch?v=ID atau youtu.be/ID atau live/ID
        return try {
            val uri = Uri.parse(url)
            when {
                uri.host?.contains("youtube.com") == true -> {
                    if (uri.path?.startsWith("/live/") == true) {
                        uri.lastPathSegment
                    } else {
                        uri.getQueryParameter("v")
                    }
                }
                uri.host?.contains("youtu.be") == true -> {
                    uri.lastPathSegment
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun loadYoutubeHome() {
        webView.loadUrl("https://www.youtube.com")
        btnMenu.visibility = View.VISIBLE
    }

    private fun loadTwitchHome() {
        webView.loadUrl("https://www.twitch.tv")
        btnMenu.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private var isAdBlockEnabled = true

    private fun injectAdBlock(url: String) {
        if (!isAdBlockEnabled) return

        val jsCode = """
            (function() {
                if (window.__adBlockInjected) return;
                window.__adBlockInjected = true;

                const css = `
                    /* YouTube Ad Elements Removal */
                    ytd-promoted-sparkles-web-renderer,
                    ytd-display-ad-renderer,
                    ytd-compact-promoted-video-renderer,
                    ytd-ad-slot-renderer,
                    #player-ads,
                    .video-ads.ytp-ad-module,
                    .ytp-ad-overlay-image,
                    .ytp-ad-text-overlay,
                    tp-yt-paper-dialog:has(yt-mealbar-promo-renderer),
                    /* Twitch Ad Elements Removal */
                    .video--ad-banner,
                    .ad-banner,
                    [data-a-target="video-ad-label"],
                    div[class*="ad-container"] {
                        display: none !important;
                        visibility: hidden !important;
                        opacity: 0 !important;
                        pointer-events: none !important;
                    }
                `;

                const style = document.createElement('style');
                style.type = 'text/css';
                style.appendChild(document.createTextNode(css));
                (document.head || document.documentElement).appendChild(style);

                // Auto skip YouTube ads & speed up when ad is playing
                setInterval(() => {
                    const video = document.querySelector('video');
                    const adOverlay = document.querySelector('.ad-showing, .ytp-ad-player-overlay');
                    
                    if (adOverlay && video) {
                        video.playbackRate = 16.0;
                        video.muted = true;
                        
                        const skipBtn = document.querySelector('.ytp-ad-skip-button, .ytp-ad-skip-button-modern, button.ytp-ad-skip-button');
                        if (skipBtn) {
                            skipBtn.click();
                        } else {
                            try {
                                video.currentTime = video.duration || video.currentTime + 5;
                            } catch (e) {}
                        }
                    }
                }, 250);
            })();
        """.trimIndent()

        webView.evaluateJavascript(jsCode, null)
    }
}
