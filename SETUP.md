# Quick Setup Guide 🔧

This guide helps you quickly configure the project for first-time setup.

## Step 1: Firebase Configuration

1. Download `google-services.json` from Firebase Console
2. Place it in: `app/google-services.json`
   ```bash
   cp ~/Downloads/google-services.json app/google-services.json
   ```

## Step 2: Cloudinary Configuration

1. Open `app/src/main/res/values/strings.xml`
2. Replace the placeholders:
   ```xml
   <string name="cloudinary_cloud_name">YOUR_CLOUD_NAME</string>
   <string name="cloudinary_upload_preset">YOUR_UNSIGNED_UPLOAD_PRESET</string>
   ```
   With your actual values:
   ```xml
   <string name="cloudinary_cloud_name">your_actual_cloud_name</string>
   <string name="cloudinary_upload_preset">your_actual_upload_preset</string>
   ```

## Step 3: Verify Setup

Check that these files exist:
- ✅ `app/google-services.json` (not in git, you created it)
- ✅ `app/src/main/res/values/strings.xml` (updated with your Cloudinary credentials)

## ⚠️ Important

- **NEVER** commit `app/google-services.json` to git
- **NEVER** commit actual Cloudinary credentials (use placeholders in strings.xml)
- The `.gitignore` file is configured to exclude sensitive files automatically

## Testing

After configuration, build and run:
```bash
./gradlew build
```

