package com.example.pemutarvideo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlInput: EditText
    private lateinit var btnLoadUrl: Button
    private lateinit var btnTwitch: Button
    private lateinit var btnYoutube: Button
    private lateinit var btnNativePlayer: Button
    private lateinit var navContainer: LinearLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlInput = findViewById(R.id.urlInput)
        btnLoadUrl = findViewById(R.id.btnLoadUrl)
        btnTwitch = findViewById(R.id.btnTwitch)
        btnYoutube = findViewById(R.id.btnYoutube)
        btnNativePlayer = findViewById(R.id.btnNativePlayer)
        navContainer = findViewById(R.id.navContainer)

        // Konfigurasi WebView
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Tombol Muat URL Manual
        btnLoadUrl.setOnClickListener {
            val customUrl = urlInput.text.toString().trim()
            if (customUrl.isNotEmpty()) {
                val finalUrl = if (!customUrl.startsWith("http://") && !customUrl.startsWith("https://")) {
                    "https://$customUrl"
                } else {
                    customUrl
                }
                webView.loadUrl(finalUrl)
            }
        }

        // Tombol Twitch Embed
        btnTwitch.setOnClickListener {
            val twitchHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        #twitch-embed { width: 100%; height: 100%; }
                    </style>
                </head>
                <body>
                    <div id="twitch-embed"></div>
                    <script src="https://embed.twitch.tv/embed/v1.js"></script>
                    <script type="text/javascript">
                        new Twitch.Embed("twitch-embed", {
                            width: "100%",
                            height: "100%",
                            channel: "monstercat",
                            parent: ["localhost", "10.0.2.2", "example.com"]
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://www.twitch.tv", twitchHtml, "text/html", "UTF-8", null)
        }

        // Tombol YouTube Embed
        btnYoutube.setOnClickListener {
            val youtubeHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        iframe { width: 100%; height: 100%; border: none; }
                    </style>
                </head>
                <body>
                    <iframe src="https://www.youtube.com/embed/dQw4w9WgXcQ?autoplay=1" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://www.youtube.com", youtubeHtml, "text/html", "UTF-8", null)
        }

        // Contoh simulasi tombol player native / toggle kontrol
        btnNativePlayer.setOnClickListener {
            if (navContainer.visibility == View.VISIBLE) {
                navContainer.visibility = View.GONE
            } else {
                navContainer.visibility = View.VISIBLE
            }
        }

        // Default muat halaman awal (YouTube embed)
        btnYoutube.performClick()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
