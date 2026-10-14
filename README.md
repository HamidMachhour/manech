# Manech

Manech is an Android inventory and order-management app for small stores. Store owners can manage products and stock, receive and process orders, and review store activity. Customers can browse published stores and items, save favorites, and place orders.

## Features

- **Store and inventory management:** create stores, set a current store, manage items, stock quantities, images, and HTML descriptions.
- **Browsing and shopping:** browse public stores and items, search and filter products, scan barcodes, add items to favorites and a cart, and place orders.
- **Item QR transfer:** generate a printable PDF of QR labels for active items and import an item into the current store by scanning its Manech JSON QR code.
- **Order management:** view customer orders and store sales, filter by status and date-independent criteria, and update the seller-side order status through its lifecycle.
- **Stock handling:** deduct item quantities when an order is placed and return quantities for cancelled or returned orders.
- **Home dashboard:** view inventory, order, and sales statistics; filter orders and sales by dates; graph sales by day, month, or year; and view best-selling items.
- **Favorites and trash:** manage favorite stores/items and restore or permanently remove trashed stores/items.
- **Language selection:** switch between English and Arabic from the menu or the sign-in/sign-up screens. Arabic resources enable right-to-left layout. Some less-used screens may still display English text.

## Item QR codes and filtering

In **My Items**, use **Print item QR codes / Save PDF** to open the Android print dialog. Choose a printer to print the labels, or choose **Save as PDF** to save them as a PDF file. The document contains QR labels for active items in the loaded list, with each QR encoding a versioned JSON payload containing the item code, title, description, prices, quantities, and location.

Use **Add item from QR** to scan a Manech item QR code. The app creates a new active item in the current store with a fresh ID; source store/item IDs and image data are not copied. Item and store searches are available in their collapsible filter cards rather than in duplicate toolbar search buttons.

## Technology

- Native Android application written in Java.
- Android Gradle Plugin 8.5.2 and Gradle Wrapper 8.9.
- Compile/target SDK 34; minimum SDK 24.
- Java source and target compatibility 17.
- Room for local SQLite persistence, with AndroidX ViewModel and LiveData for UI data flow.
- Material Components, Google Maps/location and barcode-scanning APIs, Picasso, and an image slider.

The app's primary persistence is a local Room database (`manech.db`). This repository does not define or document a Laravel API endpoint configuration.

## Requirements

- JDK 17.
- Android SDK Platform 34 and Android Build Tools installed.
- Android Studio or a compatible command-line Android/Gradle environment.
- A connected Android device or emulator for installation and runtime testing.

The Android SDK location can be configured in `local.properties` (for example, with `sdk.dir=/path/to/Android/Sdk`). Do not commit machine-specific local configuration.

## Build

From the repository root:

```sh
./gradlew :app:assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To install the debug build on the currently connected device:

```sh
./gradlew :app:installDebug
```

To start the app after installation:

```sh
adb shell am start -n com.manech.manechapp/.MainActivity
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Project layout

```text
app/src/main/java/com/manech/manechapp/
  data/        Room entities, database, and domain data
  dao/         Room DAO interfaces and queries
  helper/      Shared helpers (preferences, localization, filters, HTML rendering)
  model/       ViewModels and dependency wiring
  repository/  Data access and business operations
  view/        Activities, fragments, adapters, and custom views

app/src/main/res/
  layout/      Android view layouts
  menu/        Toolbar and navigation menus
  values/      Default (English) strings and other resources
  values-ar/   Arabic strings
```

## Data and compatibility notes

- The local database currently uses Room schema version 1 and is configured with `fallbackToDestructiveMigration()`. A schema version change without a migration can erase the app's local database on upgrade; add and test migrations before distributing schema-changing updates.
- The app manifest declares internet, camera, storage, and location permissions. Maps and barcode-related features may require the corresponding Google Play services and valid project configuration.
- Before publishing, configure Maps credentials outside source control and review permissions, backup policy, signing, and release build settings.

## Tests

No automated test sources are currently present under `app/src`. Build validation can be run with:

```sh
./gradlew :app:assembleDebug
```
