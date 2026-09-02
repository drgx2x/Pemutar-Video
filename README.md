# Project Pemutar Video (Native Android Kotlin)

Aplikasi Android sederhana menggunakan **Kotlin** dan **WebView** untuk memuat serta beralih secara dinamis antara pemutar web seperti **Twitch** dan **YouTube**, lengkap dengan kontrol UI kustom, fitur Sleep Timer, dan pemblokir iklan (ad-blocker).

## Fitur Utama
- **Multi-Platform:** Beralih cepat antara YouTube dan Twitch.
- **Sleep Timer:** Pengatur waktu otomatis untuk menutup aplikasi.
- **Ad-Blocker:** Injeksi skrip otomatis untuk menyembunyikan elemen iklan dan mempercepat durasi iklan di YouTube.
- **Fullscreen Support:** Dukungan penuh untuk mode layar penuh saat menonton video.
- **Custom Menu:** Tombol menu untuk navigasi dan pengaturan timer.

## Struktur Proyek
- `app/src/main/java/com/example/pemutarvideo/MainActivity.kt` : Logika utama, konfigurasi WebView, manajemen fullscreen, serta skrip injeksi ad-blocker.
- `app/src/main/res/layout/activity_main.xml` : Layout antarmuka pengguna (UI) dengan tombol kontrol dan WebView utama.
- `app/src/main/AndroidManifest.xml` : Konfigurasi izin akses internet dan aktivitas aplikasi.
- `app/build.gradle` : Konfigurasi modul Gradle untuk dependensi AndroidX dan Kotlin.

## Cara Menggunakan
1. Buka proyek ini menggunakan **Android Studio**.
2. Sinkronkan dependensi Gradle (*Sync Project with Gradle Files*).
3. Jalankan aplikasi di Emulator atau Perangkat Android fisik.
4. Gunakan tombol menu untuk beralih platform atau mengatur Sleep Timer.
