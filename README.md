📱 Smart Face Attendance System

A secure Android-based attendance management system that uses Face Recognition (FaceNet) to automatically mark attendance.
Built with Kotlin, Jetpack Compose, and Firebase, this app supports role-based access for Students and Teachers/Admins.

🚀 Features
👨‍🎓 Student

Secure login & registration

Face registration using FaceNet embeddings

Face-based attendance verification

View personal attendance history

Prevents duplicate attendance marking

🧑‍🏫 Teacher / Admin

Teacher dashboard

View daily attendance of all students

See student name, time, and status

Role-based UI and access control

🔐 Security & Accuracy

Firebase Authentication

Face embeddings stored securely in Firestore

ML Kit Face Detection + FaceNet matching

UID-based identity verification

🛠️ Tech Stack

Language: Kotlin

UI: Jetpack Compose

Camera: CameraX

ML: FaceNet (TFLite), ML Kit Face Detection

Backend: Firebase Authentication & Firestore

Architecture: MVVM-friendly Compose structure

📂 Firestore Database Structure
users/{uid}
 ├── name
 ├── role (student | teacher | admin)
 └── faceRegistered (true/false)

face_encodings/{uid}
 └── embedding (Float Array)

attendance/{date}/students/{uid}
 ├── name
 ├── time
 ├── status (Present)
 └── timestamp

📸 Screenshots

Add screenshots in a screenshots/ folder and link them here

screenshots/
 ├── login.png
 ├── student_home.png
 ├── face_register.png
 ├── face_match.png
 ├── teacher_dashboard.png
 └── attendance_history.png


Example:

![Login](screenshots/login.png)

⚙️ Setup Instructions

Clone the repository

git clone https://github.com/KARTHIKRAIG/smart-face-attendance-system.git


Open in Android Studio

Add Firebase

Create Firebase project

Enable Authentication (Email/Password)

Enable Firestore

Download google-services.json

Place it inside app/

Run the app

Use a real device (camera required)

🧪 Face Recognition Flow

Detect face using ML Kit

Generate embedding using FaceNet

Compare embeddings using Euclidean distance

Mark attendance if match is within threshold

👨‍💻 Developer

Karthik Rai
📧 Email: karthik9860rai@gmail.com

🔗 GitHub: https://github.com/KARTHIKRAIG

📜 License

This project is licensed under the MIT License — see the LICENSE
 file for details.

📌 Note

This project was developed for academic and learning purposes and demonstrates real-world use of Face Recognition in Android applications.

📄 MIT LICENSE (Create a file named LICENSE)
MIT License

Copyright (c) 2026 Karthik Rai

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
