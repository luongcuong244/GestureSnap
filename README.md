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

https://github.com/LuongCuong244/GestureSnap/assets/83854080/ba300f5b-13b3-4384-9dd7-241d03c45bf6

----------------
> *Gesture detection and photo capture*

https://github.com/LuongCuong244/GestureSnap/assets/83854080/41ee43c6-0000-42a5-9cc9-f2b61e77a1cf

### Screenshots
----------------
> *Camera landscape mode*
<img width="610" alt="Screenshot 2023-08-18 at 01 02 48" src="https://github.com/LuongCuong244/GestureSnap/assets/83854080/685756d2-0ae1-47ff-8d9b-655c37fd4c6a">

----------------
> *Hand gesture detection*
<img width="344" alt="Screenshot 2023-08-18 at 00 59 13" src="https://github.com/LuongCuong244/GestureSnap/assets/83854080/ba5f0cdc-a17a-439e-895e-2b7e698ec32f">


----------------
> *Photos deletion*
<img width="343" alt="Screenshot 2023-08-18 at 01 04 24" src="https://github.com/LuongCuong244/GestureSnap/assets/83854080/2a6edb8c-ea16-45e8-95cb-3fa620970240">


----------------
> *Photo details*
<img width="343" alt="Screenshot 2023-08-18 at 01 06 00" src="https://github.com/LuongCuong244/GestureSnap/assets/83854080/dfadc45b-7be7-47aa-a37e-58c5ac618a87">


## License

    GestureSnap
    Copyright (C) 2023-2025  Cuong

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
