# Beacon-Face-Attendance-systems
# 🎓 Smart Attendance Management System Using Face Recognition

An intelligent, automated attendance management system that uses **Face Recognition and Computer Vision** to identify students and record their attendance accurately. The system aims to reduce manual work, prevent proxy attendance, and make classroom attendance management more efficient.

## 🚀 Project Overview

The Smart Attendance Management System automates the traditional attendance process using facial recognition technology. A camera captures the student's face, the system identifies the individual using a trained recognition model, and attendance is automatically recorded with the corresponding date and time.

The project integrates Python, OpenCV, machine learning, and a database to provide a simple and efficient attendance management solution.

## ✨ Key Features

- 👤 **Face Recognition:** Identifies students using facial recognition technology.
- 📸 **Real-Time Detection:** Captures and detects faces through a camera.
- 📝 **Automated Attendance:** Records attendance without manual intervention.
- 🔐 **Proxy Attendance Prevention:** Helps reduce attendance fraud through facial verification.
- ⏰ **Timestamp Recording:** Stores attendance with date and time.
- ☁️ **Cloud Integration:** Supports cloud-based attendance storage and management.
- 📊 **Attendance Management:** Maintains organized attendance records.
- 📁 **CSV Export:** Stores attendance records in CSV format for easy access.

## 🛠️ Technologies Used

| Category | Technologies |
|---|---|
| Programming Language | Python |
| Computer Vision | OpenCV |
| Face Recognition | face_recognition |
| Machine Learning | KNN Algorithm |
| Backend | Flask |
| Database | Firebase |
| Data Storage | CSV |
| Development Tools | VS Code / PyCharm |

## 🏗️ System Architecture

The system follows this workflow:

1. **Face Registration:** Capture and store student facial images.
2. **Dataset Preparation:** Process facial images and extract face encodings.
3. **Face Detection:** Capture live video using a webcam.
4. **Face Recognition:** Compare detected faces with registered student data.
5. **Attendance Recording:** Mark attendance when a registered student is identified.
6. **Data Storage:** Save attendance details with timestamps.
7. **Attendance Management:** View and manage recorded attendance.

## 🔄 Working Flow

```text
        Start
          |
          v
   Camera Initialization
          |
          v
    Capture Live Image
          |
          v
      Face Detection
          |
          v
     Face Recognition
          |
          v
   Is Student Identified?
       /        \
     Yes         No
      |           |
      v           v
 Mark Attendance  Unknown
      |
      v
 Save Date & Time
      |
      v
 Update Attendance
      |
      v
        End
```

## 📂 Project Structure

```text
Smart-Attendance-System/
│
├── dataset/
│   └── Student facial images
│
├── attendance/
│   └── Attendance records
│
├── static/
│   └── CSS, images and other assets
│
├── templates/
│   └── HTML templates
│
├── app.py
├── face_recognition.py
├── train_model.py
├── requirements.txt
├── attendance.csv
└── README.md
```

*Note: Adjust the folder structure according to your actual repository.*

## ⚙️ Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR-USERNAME/Smart-Attendance-System.git
```

### 2. Navigate to the Project Directory

```bash
cd Smart-Attendance-System
```

### 3. Create a Virtual Environment

```bash
python -m venv venv
```

Activate it:

**Windows**
```bash
venv\Scripts\activate
```

**macOS / Linux**
```bash
source venv/bin/activate
```

### 4. Install Dependencies

```bash
pip install -r requirements.txt
```

### 5. Run the Application

```bash
python app.py
```

## 🎯 Project Objectives

- Automate classroom attendance management.
- Reduce time spent on manual attendance.
- Minimize proxy attendance.
- Improve attendance accuracy and reliability.
- Provide an organized digital attendance system.
- Explore practical applications of AI and computer vision in education.

## 🔮 Future Enhancements

- 📱 Android application integration.
- 📍 GPS and geofencing-based classroom verification.
- 📡 Bluetooth Low Energy (BLE) classroom detection.
- ☁️ Real-time Firebase synchronization.
- 📈 Advanced attendance analytics and reports.
- 🔔 Automated attendance notifications.
- 🧠 Improved recognition with liveness detection.

## 👨‍💻 Contributors

**Rohan C Biju**

Computer Science and Engineering

Interested in Software Development, Artificial Intelligence, Machine Learning, and Computer Vision.

## 📌 Project Status

🚧 **Under Development**

More features and improvements will be added as development progresses.

## 📄 License

This project is intended for educational and research purposes. A suitable open-source license can be added as the project develops.

---

⭐ If you find this project interesting, consider giving this repository a star!
