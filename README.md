

# 📱 Gold Loan Calculator App

A modern Android application that helps users **calculate gold loan eligibility** based on gold purity, weight, and available loan schemes.
The app is built with **Java + Firebase + SQLite**, and features a clean, user-friendly interface.

---

## ✨ Features

* 📊 **Loan Calculator**: Calculate eligible loan amount based on entered weight and purity.
* 🏦 **Multiple Schemes**: Manage and view loan schemes with different LTV (Loan-to-Value) ratios.
* ⚖️ **Custom Purity Settings**: Adjust purity multipliers to match jewellers/banks.
* 📈 **Interest Calculation**: Automatically calculates monthly interest from yearly rates.
* 🔍 **Eligibility Check**: Shows schemes only if the loan is within min/max limits.
* ☁️ **Firebase Integration**: Stores app version, update link, and scheme info online.
* 📂 **Local Storage with SQLite**: Offline storage of schemes for quick access.

---

## 🛠️ Tech Stack

* **Language**: Java
* **Database**: SQLite (for local schemes)
* **Cloud**: Firebase Realtime Database (for version control & download link)
* **UI**: RecyclerView, CardView, Material Components

---

## ⬇️ Download

* **Clic to Download**(https://github.com/Nikhilk32535/Loan-Calculator/releases/download/v1.2/Loan-Calculator.apk)

---
## 📦 Project Structure

```
com.example.loancalculator
│
├── adapter/              # RecyclerView Adapters
├── Fragment/             # Fragments (User Manual, Set Purity, etc.)
├── purity/               # Purity settings management
├── Scheme.java           # Entity model for loan schemes
├── SchemeDatabase.java   # Local SQLite database
└── MainActivity.java     # App entry point
```

---

## 🚀 How It Works

1. **Select Gold Purity** → Adjust purity multiplier (e.g., 91.6% for 22K).
2. **Enter Gold Weight** → The app calculates eligible loan values.
3. **View Schemes** → Displays valid schemes with loan per gram & monthly interest.
4. **Check Updates** → Verifies app version from Firebase and redirects to download link if update exists.

---

## 🔧 Setup & Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/YourGitHubUsername/Loan-Calculator.git
   ```
2. Open the project in **Android Studio**.
3. Add your **Firebase Realtime Database** URL in `google-services.json`.
4. Sync Gradle and run the project on your device/emulator.

---

## 🔐 Firebase Setup

* Node: `AppVersion` → Holds the latest version (e.g., `v1.1`)
* Node: `AppDownloadLink` → Holds APK download link (Play Store / GitHub / Firebase Storage)
* Schemes stored locally in SQLite



---

## 📷 Screenshots

👉 *(Paste your screenshots below when ready)*

| Home Screen                             | Loan Calculation                               | Update Checker                            |
| --------------------------------------- | ---------------------------------------------- | ----------------------------------------- |
| ![Screenshot 1](./screenshots/home.png) | ![Screenshot 2](./screenshots/calculation.png) | ![Screenshot 3](./screenshots/update.png) |

---

## 🧑‍💻 Author

**Nikhil Kumar**
🔗 [GitHub Profile](https://github.com/Nikhilk32535)

---


