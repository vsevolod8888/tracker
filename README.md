# Route Tracker

## Overview
Route Tracker is an Android application that records your routes in the background, calculates the distance traveled, and displays the routes on a map. Additionally, you can manually draw custom routes on the map.

### Features:
- **Background Route Recording**: Automatically tracks your movement and records the route.
- **Distance Calculation**: Computes the total distance of the recorded route.
- **Map Visualization**: Displays recorded routes in **blue**.
- **Custom Route Drawing**: Allows users to manually draw their own routes on the map in **red**.

## Screenshots

<img src="https://i.imgur.com/oVXu0VK.jpg" width="300">
<img src="[Imgur](https://i.imgur.com/l9fSyHE.jpg)" width="300">
<img src="[Imgur](https://i.imgur.com/QadOz6M.jpg)" width="300">
<img src="[Imgur](https://i.imgur.com/eGDHOAr.jpg)" width="300">


## Technologies & Dependencies
The app is built using **Jetpack Compose** and leverages the following libraries:

- **Jetpack Compose** for UI development:
    - `androidx.compose.ui`
    - `androidx.compose.material3`
    - `androidx.activity.compose`
- **Dependency Injection**:
    - `Hilt` (`hilt.android`, `hilt.compiler`)
- **Navigation**:
    - `androidx.navigation.compose`
- **Google Maps & Location Services**:
    - `com.google.android.gms:play-services-maps`
    - `com.google.maps.android:maps-compose`
    - `com.google.android.gms:play-services-location`
- **Data Persistence**:
    - `androidx.room` (`room.runtime`, `room.ktx`, `room.compiler`)
    - `androidx.datastore.preferences`
- **Date & Time Handling**:
    - `kotlinx.datetime`

## Installation & Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/vsevolod8888/tracker.git
   ```
2. Open the project in **Android Studio**.
3. Add your **Google Maps API Key** to `local.properties`:
   ```properties
   MAPS_API_KEY=your_secret_key
   ```
4. Run the project on an emulator or physical device.

## License
This project is licensed under the **MIT License**.


