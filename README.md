# 💰 Gold Loan Calculator App

A simple and powerful Android app to calculate eligible gold loan amounts based on purity and weight. Designed for everyday users and admins, this app helps estimate loan eligibility and manage loan schemes with ease.

---

## 📲 Features

### 🧮 Smart Gold Weight Calculation
- Input **Gross Weight** and **Stone Weight**
- App automatically calculates:
  ```
  Net Weight = Gross Weight - Stone Weight
  ```

### 🪙 Purity-Based Loan Estimation
- Select **Gold Purity** (e.g., 22K, 24K)
- App uses **purity multiplier** to compute gold value
- Displays eligible loan amounts based on available schemes

---

## 📋 Loan Scheme Cards

Each scheme card includes:
- 🏷 **Scheme Name**
- 📏 **Loan Amount (LTV-based)**
- 📈 **Yearly Interest Rate** (converted to Monthly)
- 💸 **Minimum & Maximum Loan Limits**

---

## 🛠 Admin Controls

### ➕ Add New Loan Scheme
- Enter one price type (e.g., 75LTV)
- App auto-generates other LTV values (50%, 60%, 65%)
- Customize:
  - Interest Rate
  - Min & Max Limits

### 🧪 Update Gold Purity Multipliers
- Change multiplier values for 22K, 24K, etc.
- Keeps app responsive to market price changes

---

## 📘 User Manual
A built-in **Manual Page** explains:
1. Enter gross & stone weight
2. Select purity
3. View eligible schemes
4. Admins can add/edit schemes
5. Update gold purity multipliers

> Helps new users get started easily!

---

## 🧭 Navigation
- Uses a **Navigation Drawer** (not bottom navigation)
- Displays **user's name** at the **bottom** of the drawer menu

---

## 🛠 Tech Stack
- 💻 Android (Java)
- 📁 RoomDB – For local data storage
- 📐 XML – Custom UI layouts
- 🧩 Fragments – Clean navigation
- 📊 Custom business logic – LTV-based pricing

---

## 🚀 Future Enhancements *(optional ideas)*
- 🔄 Firebase sync (for real-time updates)
- 🖤 Dark mode
- 📄 Export scheme report as PDF

---

## 📸 Screenshots
![WhatsApp Image 2025-06-01 at 17 42 37_5510951c](https://github.com/user-attachments/assets/ce51cad7-354e-415b-9f6e-c165db7e65bf)


---

## 📝 License
Free to use, modify, and distribute.

---

**Made with 💡 by Nikhil Kumar**
