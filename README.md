# Gesture Snap
Gesture Snap is a unique and innovative Android mobile application that allows you to capture photos using hand gestures. The application utilizes AI to detect and recognize hand gestures, enabling you to capture and save photos from your device.
## Installation
- Gesture Snap is not currently available on the Google Play Store now, but I will push it there as soon as possible.
- However, you can download and try it out from [here](https://drive.google.com/file/d/10i1GNjmfv9eY6HOTat7YlQgoTHlcKCqP/view?usp=sharing).

## Features
- **Gesture-based Photo Capture:** Utilizes AI technology to detect hand gestures for photo capturing. If the detected gesture matches the selected option, it captures a photo.
- **Image Saving:** Save captured photos to your device's storage.
- **Shooting Modes:** Includes grid mode, flash mode, and timer mode for versatile photo capturing options.
- **Persistent State:** Preserves the camera mode settings and selected options across app sessions.
- **Image Gallery:** View and manage all photos stored on your device.
- **Single Image Viewing:** Select and view individual photos in full-screen mode.
- **Image Deletion:** Delete unwanted photos directly from the app.
> :warning: : With `Android 10 (API 29)`, you are currently unable to delete multiple photos at once. I apologize for the inconvenience and will fix this issue as soon as possible.

## Dependencies
- [CameraX API](https://developer.android.com/training/camerax) : Simplifies camera development with a consistent API across different Android devices and versions.
- [Mediapipe](https://developers.google.com/mediapipe/solutions/vision/gesture_recognizer) : Enables hand gesture detection and recognition for intuitive interaction with the camera app.
- [Jetpack Compose](https://developer.android.com/jetpack/compose/documentation) : Facilitates the creation of a modern and user-friendly interface with a declarative UI toolkit.
- [View Model](https://developer.android.com/topic/libraries/architecture/viewmodel) : Manages and persists UI-related data, ensuring data consistency and preserving app state.
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) : Provides powerful crash reporting and analysis for improved app stability and debugging.
- [Google AdMob](https://developers.google.com/admob) : Enables monetization through in-app advertising, displaying relevant ads to generate revenue.
  
> ⚠️ : For security reasons, I have not pushed the `google-services.json` file to the source code.
> Therefore, to use your own `Firebase Crashlytics`, please add your own `google-services.json` file to the project as [instructed here](https://firebase.google.com/docs/android/setup).

## What does the application have?
### Demo Videos
----------------
> *Request permissions and gallery screen*

https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/6823426b-ee5d-43b3-9767-c5aa91e60adc

----------------
> *Gesture detection and photo capture*

https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/b1925bb6-57a7-45c0-a4ef-a32f5effa6fb

### Screenshots
----------------
> *Camera landscape mode*
<img width="610" alt="Screenshot 2023-08-18 at 01 02 48" src="https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/63bc8f5a-f8de-41b5-a9fb-602d9de20e93">

----------------
> *Hand gesture detection*
<img width="344" alt="Screenshot 2023-08-18 at 00 59 13" src="https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/2861db0c-2721-4286-9843-14bea60187c4">

----------------
> *Photos deletion*
<img width="343" alt="Screenshot 2023-08-18 at 01 04 24" src="https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/7a6ba628-bbbf-4cbd-8efd-9bec4bd71cb8">

----------------
> *Photo details*
<img width="343" alt="Screenshot 2023-08-18 at 01 06 00" src="https://github.com/LuongCuong244/Gesture-Snap/assets/83854080/b5406fdc-fa71-4f67-ac4f-5b1e19c4bae4">
