# Smart Face Attendance System 📸✅

An Android-based Smart Attendance System using **Face Recognition** to mark attendance automatically.

---

## 📌 Features

### Student
- Face Registration
- Face-based Attendance
- View Personal Attendance History

### Teacher
- View Daily Attendance
- Teacher Dashboard

---

## 🧠 Technologies Used

- Android (Kotlin, Jetpack Compose)
- Firebase Authentication
- Firebase Firestore
- FaceNet (TensorFlow Lite)
- ML Kit Face Detection
- CameraX

---

## 📂 Firestore Structure

users/{uid}
- name
- role
- faceRegistered

face_encodings/{uid}
- embedding

attendance/{date}/students/{uid}
- name
- time
- status

---

## 👨‍💻 Developer

**Karthik Rai**  
Email: karthik9860rai.com  
GitHub: https://github.com/KARTHIKRAIG

---

## 📜 Note
This project is created for academic and learning purposes.
