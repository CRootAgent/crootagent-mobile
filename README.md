# CRootAgent Mobile 🦀📱

Aplikasi Android native (Kotlin) sebagai remote control dan mobile console untuk **CRootAgent**. Aplikasi ini memungkinkan pengembang memantau repositori, menjalankan perintah shell, dan berinteraksi langsung dengan agent cerdas dari perangkat smartphone.

---

## Fitur Utama

- **Remote Agent Console:** Antarmuka chat interaktif dengan rendering gelembung percakapan berkecepatan tinggi berbasis `RecyclerView` dan `DiffUtil`.
- **Dukungan Multi-Provider LLM:** Terhubung langsung ke Google Gemini, Groq, OpenAI, maupun instance Ollama lokal melalui pengaturan API terintegrasi.
- **Tombol Pintas CLI:** Menjalankan perintah rutin dalam satu ketukan (`git status`, `cargo test`, `crootagent models`, `session list`).
- **Simulasi ReAct Mandiri:** Mode fallback cerdas yang mengevaluasi kebutuhan tool dan alur eksekusi saat bekerja tanpa koneksi API eksternal.
- **Arsitektur Bersih MVVM:** Memisahkan data layer (`AgentRepository`), domain logic (`AgentViewModel`), dan UI (`MainActivity`, `ChatAdapter`) dengan siklus hidup AndroidX dan Kotlin Coroutines.
- **Automated CI/CD & Auto-Tagging:** Workflow GitHub Actions otomatis menguji kode, mengompilasi APK (Debug & Release), membuat tag versi git, dan merilis aset APK ke GitHub Releases.

---

## Arsitektur Aplikasi

```
crootagent-mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/crootagent/mobile/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/          # Retrofit endpoints (Gemini, OpenAI)
│   │   │   │   │   ├── model/        # Data classes (ChatMessage, API schemas)
│   │   │   │   │   └── repository/   # Repository layer & CLI interpreter
│   │   │   │   ├── ui/
│   │   │   │   │   ├── adapter/      # RecyclerView ChatAdapter
│   │   │   │   │   └── viewmodel/    # MVVM AgentViewModel
│   │   │   │   └── MainActivity.kt   # Activity utama & dialog konfigurasi
│   │   │   ├── res/                  # Layouts, vector drawables, colors, themes
│   │   │   └── AndroidManifest.xml   # Definisi izin INTERNET dan package
│   │   └── test/java/com/crootagent/mobile/
│   │       └── AgentViewModelTest.kt # Pengujian unit test berbasis Coroutines
│   └── build.gradle.kts              # Konfigurasi build module app (compileSdk 34)
├── .github/workflows/
│   └── android-release.yml           # CI/CD otomatis untuk build & publikasi APK
├── build.gradle.kts                  # Root build script
├── settings.gradle.kts               # Definisi modul
└── gradlew                           # Gradle wrapper executable
```

---

## Menjalankan Pengujian Unit

Aplikasi dilengkapi unit test terisolasi menggunakan `StandardTestDispatcher` dan `InstantTaskExecutorRule`:

```bash
cd crootagent-mobile
./gradlew test
```

Hasil test report HTML dapat dilihat di `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## Mengompilasi APK Secara Lokal

Pastikan Java 17 dan Android SDK (API 34) terpasang di sistem:

```bash
# Debug APK
./gradlew assembleDebug

# Output APK:
# app/build/outputs/apk/debug/app-debug.apk

# Release APK (Unsigned)
./gradlew assembleRelease

# Output APK:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## CI/CD Workflow & Rilis Otomatis

File `.github/workflows/android-release.yml` bekerja dengan mekanisme berikut:
1. Dipicu setiap ada push ke branch `main`, tag `v*`, atau pull request.
2. Menggunakan runner Ubuntu dengan Temurin JDK 17.
3. Menjalankan lint dan unit test (`./gradlew test`).
4. Mengompilasi debug dan release APK.
5. Mengunggah artefak build ke sistem GitHub Actions.
6. Menentukan tag otomatis berbasis format waktu (`v1.0.YYYYMMDDHHMM`) atau tag git yang dikirimkan.
7. Membuat GitHub Release baru dan melampirkan file APK yang siap diunduh pengguna.

---

## Identitas Pengembang

- **Nama:** Ahmad Zakky Nurfauzan
- **NPM:** 714250028
- **Program Studi:** Sarjana Terapan Teknik Informatika (D4TI)
- **Institusi:** Universitas Logistik dan Bisnis Internasional (ULBI), Bandung
- **Dosen Pembimbing:** Rolly Maulana Awangga, S.T., M.T., CAIP
