# SanKhadip — Android Study App

> _It's not over until I win._

A native Android app to organize your study material by subject, with a dark
neon-blue theme inspired by the SanKhadip badge. Built with **Kotlin**,
**Jetpack Compose** (Material 3) and **Room**.

## Features

- **5 fixed subject folders**: Physics, Chemistry, Maths, Biology, English
  (each with its own accent color; they can't be deleted or renamed).
- **Nested folders** — create and delete subfolders inside any subject
  (e.g. `Physics → Vector Physics`), at any depth.
- **File management** — upload files from the device (Storage Access
  Framework), rename, delete, and open them with an external viewer.
- **Persistent storage** — folder/file metadata in Room; uploaded files are
  copied into the app's private internal storage.
- **Breadcrumb navigation**, in-folder **search**, and rotating motivational
  quotes in the header.

## Tech stack

| Area        | Choice                                   |
|-------------|------------------------------------------|
| Language    | Kotlin                                   |
| UI          | Jetpack Compose + Material 3             |
| Persistence | Room (SQLite) + internal file storage    |
| Min / Target SDK | 24 / 34                             |
| Build       | Gradle (Kotlin DSL) + version catalog    |

## Project structure

```
app/src/main/java/com/fyphost/sankhadip/
├── SanKhadipApp.kt          # Application + manual DI
├── MainActivity.kt          # Compose entry point
├── data/                    # Room entity, DAO, database, repository, subjects
└── ui/                      # ViewModel, Compose screens, components, theme
```

## Building

Open the project in **Android Studio** (Giraffe or newer), let it sync, then
run on an emulator or device. From the command line:

```bash
./gradlew :app:assembleDebug
```

The debug APK is produced at
`app/build/outputs/apk/debug/app-debug.apk`.

> Requires JDK 17–21 and the Android SDK (compileSdk 34, build-tools 34.0.0).
> `local.properties` with `sdk.dir` is created automatically by Android Studio.

## Using your own logo

The header and launcher icon use a built-in neon "S" badge. To use the real
SanKhadip artwork, replace the launcher icon via Android Studio's Image Asset
tool (or swap `app/src/main/res/drawable/ic_launcher.xml`).
