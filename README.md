# 💊 Pharmacy Management System

Pharmacy Management System is a desktop-based application built with **Python** that helps manage pharmacy operations like medicine inventory, billing, sales records, supplier data, and customer management.

---

## 📌 Project Overview

This Pharmacy Management System allows users to:

✔ Manage medicine inventory  
✔ Add, update or delete medicine records  
✔ Record sales & customers  
✔ Generate bills  
✔ Manage supplier and expiry details

It is designed to streamline the workflow of pharmacies and reduce manual work.

---

## 🛠️ Technologies Used

- Python
- GUI (Tkinter)
- SQLite / MySQL (Database)
- mysql-connector-python / sqlite3
- Pillow (for images)

---

## 🗃️ Project Structure

```
Pharmacy-Management-System/
├── main.py                  # Main application file
├── database.py              # Database connection & logic
├── pharmacy.db/sql file     # Database file (if using SQLite)
├── assets/                  # Images & icons
├── requirements.txt         # Python libraries used
├── README.md               # Project documentation
└── .gitignore
```

---

## ✨ Features

✔ Login system  
✔ Add / Edit / Delete Medicines  
✔ Manage Inventory  
✔ Sales & Billing System  
✔ Supplier Records  
✔ Expiry management  
✔ Search & Filter functionality  

---

## ⚙️ Installation & Setup

### 📝 Prerequisites

✔ Python 3.x installed  
✔ Required libraries

### 📦 Install Requirements

```bash
pip install -r requirements.txt
```

---

## 🏃 Run the Application

```bash
python main.py
```

---

## 🗄️ Database Setup

If you are using **SQLite**:
- Database file (`pharmacy.db`) will be created automatically on first run.

If you are using **MySQL**:
1. Open MySQL
2. Create database:

```sql
CREATE DATABASE pharmacy_db;
```

3. Import SQL file (if provided) using MySQL Workbench.

4. Update database credentials in `database.py`

---

## 📸 Screenshots

(Add screenshots of your UI screens)

- Login Screen  
- Dashboard  
- Inventory  
- Billing Page

---

## 🚀 Future Improvements

✔ Add Role-based users  
✔ Add Report export (PDF/Excel)  
✔ Barcode scanning support  
✔ Multi-user support  

---

## 👨‍💻 Author

**Jalp Patel**

---

## 📄 License

This project is for educational purposes.
