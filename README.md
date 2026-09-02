# Project Pemutar Video (Native Android Kotlin)

Aplikasi Android sederhana menggunakan **Kotlin** dan **WebView** untuk memuat serta beralih secara dinamis antara pemutar *embed* web seperti **Twitch** dan **YouTube**, lengkap dengan kontrol UI kustom dan kolom input URL dinamis.

## Struktur Proyek
- `app/src/main/java/com/example/pemutarvideo/MainActivity.kt` : Logika utama, konfigurasi WebView, serta skrip inject Twitch/YouTube embed.
- `app/src/main/res/layout/activity_main.xml` : Layout antarmuka pengguna (UI) dengan tombol kontrol tersembunyi/ditampilkan dan WebView utama.
- `app/src/main/AndroidManifest.xml` : Konfigurasi izin internet dan aktivitas utama aplikasi.
- `app/build.gradle` : Konfigurasi modul Gradle untuk dependensi AndroidX dan Kotlin.

## Cara Menggunakan
1. Buka proyek ini menggunakan **Android Studio**.
2. Sinkronkan dependensi Gradle (*Sync Project with Gradle Files*).
3. Jalankan aplikasi di Emulator atau Perangkat Android fisik Anda.
