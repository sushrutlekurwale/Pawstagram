# Pawstagram 📸🐾

A modern Android social media app built with Jetpack Compose for sharing pet photos and connecting with pet lovers. Pawstagram combines beautiful UI design with powerful backend services for a seamless user experience.

## ✨ Features

- 🔐 **Authentication**
  - Email/Password sign-up and sign-in
  - Google Sign-In support
  - Password reset functionality
  - Secure user profile management

- 📱 **Feed & Posts**
  - Browse posts in a beautiful feed
  - Upload photos with captions and hashtags
  - Like posts (with red heart animation)
  - Real-time post updates
  - Empty state handling

- 🎨 **UI/UX**
  - Modern Material Design 3
  - Dark mode support
  - Smooth animations and transitions
  - Responsive design
  - Professional color scheme (Orange/Amber & Yellow)
  - Custom logo branding

- 🌐 **Backend Services**
  - Cloudinary for image storage (free tier: 25GB storage, 25GB bandwidth/month)
  - Firebase Firestore for post metadata
  - Firebase Authentication

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend Services**:
  - Firebase Authentication
  - Firebase Firestore
  - Cloudinary (Image Storage)
- **Libraries**:
  - Jetpack Compose (UI)
  - Material Design 3
  - Navigation Compose
  - Coil (Image Loading)
  - OkHttp (Network Requests)
  - Room (Local Database - optional)
  - Kotlin Coroutines & Flow

## 📋 Prerequisites

Before you begin, ensure you have:

- **Android Studio** (Arctic Fox or later recommended)
- **JDK 11** or higher
- **Android SDK** (API level 24 or higher)
- **Firebase Account** (free tier available)
- **Cloudinary Account** (free tier available)

## 🔒 Security Notice

**⚠️ IMPORTANT**: This repository does NOT contain sensitive API keys or configuration files. Before running the app, you must configure the following:

### Required Configuration Files

1. **`app/google-services.json`** - Firebase configuration
   - This file is **excluded from git** via `.gitignore`
   - Download it from Firebase Console after creating your project
   - Place it in `app/google-services.json`
   - See `app/google-services.json.example` for structure reference

2. **`app/src/main/res/values/strings.xml`** - Cloudinary credentials
   - Contains placeholders: `YOUR_CLOUD_NAME` and `YOUR_UNSIGNED_UPLOAD_PRESET`
   - Replace these with your actual Cloudinary credentials

### Files Excluded from Git

The following files are excluded from version control:
- `app/google-services.json` (Firebase config with API keys)
- `local.properties` (Android SDK path)
- All keystore files (`*.jks`, `*.keystore`, `*.key`)
- Secret property files

### Never Commit

- ❌ Firebase API keys or `google-services.json`
- ❌ Cloudinary cloud names or API keys in committed files
- ❌ Keystore files or signing keys
- ❌ Local SDK paths

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Pawstagram
```

### 2. Firebase Setup

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project" and follow the setup wizard
   - Enable **Authentication** and **Firestore Database**

2. **Add Android App to Firebase**:
   - Click the Android icon in Firebase Console
   - Package name: `com.example.pawstagram`
   - Download `google-services.json`

3. **Get SHA-1 Fingerprint**:
   ```bash
   ./gradlew signingReport
   ```
   - Copy the SHA-1 fingerprint from the output

4. **Add SHA-1 to Firebase**:
   - In Firebase Console → Project Settings → Your app
   - Click "Add fingerprint"
   - Paste your SHA-1 and SHA-256 fingerprints

5. **Place google-services.json**:
   - **Important**: Copy the downloaded `google-services.json` to `app/google-services.json`
   - This file contains your Firebase API keys and should NOT be committed to git
   - The repository includes `app/google-services.json.example` as a template only

6. **Enable Firebase Services**:
   - **Authentication**: Enable Email/Password and Google Sign-In
   - **Firestore**: Create a database in test mode (for development)

### 3. Cloudinary Setup

1. **Create Cloudinary Account**:
   - Sign up at [Cloudinary](https://cloudinary.com/) (free tier)

2. **Create Upload Preset**:
   - Go to Settings → Upload
   - Create an **Unsigned Upload Preset**
   - Name it (e.g., `pawstagram_unsigned`)
   - Set folder: `pawstagram/images`
   - Save

3. **Get Your Cloud Name**:
   - Find your cloud name in the Cloudinary dashboard

4. **Configure in App**:
   - Open `app/src/main/res/values/strings.xml`
   - Update the following values:
     ```xml
     <string name="cloudinary_cloud_name">YOUR_CLOUD_NAME</string>
     <string name="cloudinary_upload_preset">YOUR_UNSIGNED_UPLOAD_PRESET</string>
     ```

### 4. Build and Run

1. **Open Project in Android Studio**
   - Open the `Pawstagram` folder in Android Studio
   - Wait for Gradle sync to complete

2. **Build the Project**:
   ```bash
   ./gradlew build
   ```

3. **Run on Device/Emulator**:
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
     ```bash
     ./gradlew installDebug
     ```

## 📁 Project Structure

```
Pawstagram/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/pawstagram/
│   │       │   ├── data/
│   │       │   │   ├── auth/           # Authentication repository
│   │       │   │   └── firebase/       # Firebase Firestore operations
│   │       │   ├── model/              # Data models
│   │       │   ├── ui/
│   │       │   │   ├── screens/        # Compose screens
│   │       │   │   ├── theme/          # Theme configuration
│   │       │   │   └── *ViewModel.kt    # ViewModels
│   │       │   └── MainActivity.kt     # Main activity
│   │       ├── res/
│   │       │   ├── drawable/           # Images (logo.png)
│   │       │   └── values/
│   │       │       └── strings.xml      # Cloudinary config
│   │       └── AndroidManifest.xml
│   ├── google-services.json            # Firebase config
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml             # Dependency versions
└── README.md
```

## ⚙️ Configuration

### Cloudinary Configuration

Edit `app/src/main/res/values/strings.xml`:

```xml
<string name="cloudinary_cloud_name">your_cloud_name</string>
<string name="cloudinary_upload_preset">your_upload_preset</string>
```

### Theme Colors

Customize colors in `app/src/main/java/com/example/pawstagram/ui/theme/Color.kt`:

- `PrimaryColor`: Orange/Amber (#FFA000)
- `SecondaryColor`: Yellow (#FDD835)
- `SecondaryLightColor`: Light Yellow (#FFEB3B)
- `LikeColor`: Red (#FF0000)

## 🎨 UI Features

- **Bottom Navigation Bar**: Custom yellow theme with orange selected items
- **Cards**: Clean white cards with subtle borders
- **Dark Mode**: Fully supported with appropriate color schemes
- **Animations**: Smooth scroll-to-top, like animations, and transitions

## 📱 App Screens

1. **Sign In/Sign Up Screen**: Authentication with email/password or Google
2. **Feed Screen**: Browse all posts with like functionality
3. **Upload Screen**: Add new posts with image, caption, and hashtags
4. **Navigation Drawer**: User profile and logout

## 🔧 Troubleshooting

### Common Issues

1. **"Unknown calling package name" Error**:
   - Add SHA-1 fingerprint to Firebase Console
   - Ensure `google-services.json` is in the correct location

2. **Cloudinary Upload Fails**:
   - Verify cloud name and upload preset in `strings.xml`
   - Check upload preset is set to "Unsigned"
   - Verify folder path in Cloudinary settings

3. **Posts Not Appearing**:
   - Check Firestore security rules
   - Verify Firestore indexes are created
   - Check network connectivity

4. **Build Errors**:
   - Clean and rebuild: `./gradlew clean build`
   - Invalidate caches in Android Studio
   - Ensure all dependencies are synced

## 📄 License

This project is open source and available for educational purposes.

## 👨‍💻 Developer

Built with ❤️ using Jetpack Compose and modern Android development practices.

## 🙏 Acknowledgments

- Firebase for authentication and database
- Cloudinary for image storage
- Jetpack Compose team for the amazing UI framework

---

**Note**: This project uses free-tier services. For production use, consider upgrading to paid plans for better performance and higher limits.


