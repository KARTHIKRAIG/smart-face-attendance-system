package com.shreyapoojary.smartfaceattendance.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onRegisterFace: () -> Unit,
    onVerifyFace: () -> Unit,
    onViewAttendance: () -> Unit,
    onAdminDashboard: () -> Unit,
    onAboutApp: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("Loading...") }
    var role by remember { mutableStateOf("") }
    var faceRegistered by remember { mutableStateOf(false) }

    LaunchedEffect(auth.currentUser?.uid) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                name = doc.getString("name") ?: "User"
                role = (doc.getString("role") ?: "student")
                    .lowercase(Locale.getDefault())
                faceRegistered = doc.getBoolean("faceRegistered") ?: false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /* ---------- HEADER ---------- */
        Text(
            text = "Welcome, $name",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Role: ${role.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* ================= STUDENT UI ================= */
        if (role == "student") {

            Text(
                text = "Face Registered: ${if (faceRegistered) "Yes" else "No"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!faceRegistered) {
                Button(onClick = onRegisterFace) {
                    Text("Register Face")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (faceRegistered) {
                Button(onClick = onVerifyFace) {
                    Text("Verify Attendance")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ✅ STUDENT ONLY
            Button(onClick = onViewAttendance) {
                Text("My Attendance History")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        /* ================= TEACHER / ADMIN UI ================= */
        if (role == "teacher" || role == "admin") {

            Button(onClick = onAdminDashboard) {
                Text("Teacher Dashboard")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        /* ================= ABOUT APP ================= */
        Button(onClick = onAboutApp) {
            Text("About App")
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ================= LOGOUT ================= */
        Button(
            onClick = {
                auth.signOut()
                onLogout()
            }
        ) {
            Text("Logout")
        }
    }
}
