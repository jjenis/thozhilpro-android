# ThozhilPro Android App

A modern Android app for **ThozhilPro** — a multi-tenant SaaS inventory and business management platform.

## Tech Stack

- **Kotlin** with Jetpack Compose
- **Material 3** design system
- **Hilt** for dependency injection
- **Retrofit + OkHttp** for networking
- **DataStore** for local preferences
- **Firebase Auth** + **Firebase Cloud Messaging**
- **Razorpay** for subscription payments
- **WorkManager** for background notification polling
- **Coil** for image loading

## Project Structure

```
app/src/main/java/com/thozhilpro/app/
├── data/
│   ├── api/          ApiService (Retrofit interface)
│   ├── local/        PreferencesManager (DataStore)
│   ├── model/        Data classes (Models.kt)
│   └── repository/   AuthRepository
├── di/               AppModule (Hilt DI)
├── payment/          RazorpayPaymentCallback
├── service/          FCMService
├── worker/           NotificationPollingWorker
├── ui/
│   ├── navigation/   NavGraph + Routes
│   ├── screens/      All Composable screens
│   ├── theme/        Theme, Colors
│   └── viewmodel/    All ViewModels
├── MainActivity.kt
└── ThozhilProApp.kt
```

## Features

- **Auth**: Email/password login/register, Firebase Google Sign-In
- **Dashboard**: Key business stats, quick navigation
- **Customers**: CRUD with search, balance tracking
- **Suppliers**: CRUD with search, balance tracking
- **Inventory**: Item management with pricing tiers
- **Purchases**: Purchase tracking with line items
- **Sales**: Wholesale and retail sales
- **Payments**: Record received/paid, edit/delete
- **Expenses**: Categorized expense tracking, edit/delete
- **Analytics**: Revenue, profit/loss, item movement reports
- **Notifications**: Push + in-app with read/unread
- **Settings**: Business config, GST, change password
- **Profile**: User info, subscription status, logout
- **Subscription**: Razorpay payment for plan renewal

## Setup

1. Clone the repository
2. Add your `google-services.json` to `app/`
3. Update `BASE_URL` in `AppModule.kt` to point to your backend
4. Open in Android Studio and sync Gradle
5. Build and run

## Configuration

- **API Base URL**: `AppModule.kt` → `BASE_URL`
- **Firebase**: Add `google-services.json` from Firebase Console
- **Razorpay**: SDK is included; payment flow triggers from SubscriptionExpiredScreen

## Requirements

- Android Studio Hedgehog or later
- Min SDK 26 (Android 8.0)
- Target SDK 35
- JDK 17
