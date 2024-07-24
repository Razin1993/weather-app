# weather-app
This Android Application provides users with real-time weather information based on their current location. And also get information for some listed cities.
This app is designed with a clean user interface, making it easy for users to check the weather conditions quickly.
It utilizes modern Android development components such as MVVM, Room Database, Retrofit2 and Lottie animation

**Features**
1. Current Weather: Get real-time weather information including temperature, humidity, wind speed, wind direction, air pressure, sun rise and set.
2. Weather By City: Get weather information for some listed cities
3. Location-Based Updates: Automatically fetch GPS weather data for the user's current location.
4. User-friendly UI: Clean and modern design with navigation.
5. Dark Mode: Support dark mode for better usability in low-light conditions.
6. Theme Handling: By default system theme will apply, and the user can switch to a light, dark or system-based theme.
7. Offline access: Save weather data locally using Room Db for offline access.
8. Smooth Animation: Enhanced user experience with Lottie animation

**Architecture**
This app is built using MVVM(Model-View-ViewModel) architecture to ensure a clear separation of concerns and to make the code more maintainable

**Technologies Used**
MVVM Architecture - Ensure a clean separation between the UI and business logic
Room Db - Provides local data storage for offline access
Retrofit - Handles network request to fetch weather data from the API
Lottie Animation - Add smooth and visually appealing animations to the app
Coil - Image loading library

**Download APK**
Install weather_app.apk in your android device


**Installation**
To get a local copy up and running follow these steps.

**Prerequisites:**
1. Android Studio (this is developed in Android Studio Koala)
2. Android SDK
3. Java JDK

**Clone the Repository**
git clone https://github.com/Razin1993/weather-app.git

**Open in Android Studio**
1. Open Android Studio
2. Click on File -> Open
3. Navigate to the cloned repository and click ok

**Build and Run**
In this app, Used OpenWeatherMap API to get weather info. It requires an API key. So add your API_KEY in local.properties
1. Add **API_KEY** and **BASE_URL** in local.properties
2. Make sure you have an Android device or emulator
3. Click on Run-> Run app

**Usage**
1. Launch the APP: Open the Weather app on your Android device.
2. Allow location access: When location permission is prompted, allow the app to access your location to fetch weather data for your current location.
3. View Weather info: The home screen will display the current weather and forecast.
4. Other cities: The user can able to see the weather info for some listed cities in the app.






