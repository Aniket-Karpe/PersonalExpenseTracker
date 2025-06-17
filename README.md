# Personal Expense Tracker

An Android application that allows users to manage their personal expenses. The app uses Room database for local storage and follows MVVM architecture pattern.

## Features

- Add, edit, and delete expenses
- View expenses in a list sorted by date
- Filter expenses by category
- Search expenses by title or notes
- View monthly expense summary dashboard with category breakdown
- Clean and intuitive Material Design UI
- Local Room database with CRUD functionalities

## Technical Details

### Architecture
- MVVM (Model-View-ViewModel) architecture
- Repository pattern for data management
- Room database for local storage
- LiveData for reactive UI updates
- Navigation component for screen navigation
- ViewBinding for view access
- Coroutines for asynchronous operations

### Database Schema
```sql
expenses
- id: Long (Primary Key, Auto Increment)
- title: String
- amount: Double
- category: String
- date: String (yyyy-MM-dd)
- notes: String? (Optional)
```

### Dependencies
- AndroidX Core KTX
- Room (room-runtime, room-ktx, room-compiler)
- Lifecycle Components (lifecycle-viewmodel-ktx, lifecycle-livedata-ktx, lifecycle-runtime-ktx)
- Navigation Component (navigation-fragment-ktx, navigation-ui-ktx)
- Material Design Components
- Coroutines (kotlinx-coroutines-android)
- JUnit (for unit testing)
- AndroidX Test (androidx.junit, espresso-core for instrumentation testing)
- Core Library Desugaring

## Development Environment
- **Android Studio Version:** Arctic Fox 2020.3.1 or newer (Recommended for optimal experience)
- **Gradle Version:** 8.7
- **Kotlin Version:** 1.9.22


## Requirements
- Minimum SDK: 21 (Android 5.0 Lollipop)
- Target SDK: 35 (Android 15)
