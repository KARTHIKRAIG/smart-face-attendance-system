package com.shreyapoojary.smartfaceattendance.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AttendanceItem(
    val date: String,
    val time: String,
    val status: String
)

@Composable
fun AttendanceHistoryScreen(
    onBack: () -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var loading by remember { mutableStateOf(true) }
    var attendanceList by remember { mutableStateOf<List<AttendanceItem>>(emptyList()) }

    LaunchedEffect(uid) {
        if (uid == null) {
            loading = false
            return@LaunchedEffect
        }

        println("Reading attendance for UID: $uid")

        db.collection("users")
            .document(uid)
            .collection("attendance")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->

                attendanceList = result.documents.map {
                    AttendanceItem(
                        date = it.id,
                        time = it.getString("time") ?: "--",
                        status = it.getString("status") ?: "Absent"
                    )
                }

                println("Attendance records found: ${attendanceList.size}")
                loading = false
            }
            .addOnFailureListener {
                println("Error loading attendance: ${it.message}")
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "My Attendance History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                CircularProgressIndicator()
            }

            attendanceList.isEmpty() -> {
                Text("No attendance records found")
            }

            else -> {
                LazyColumn {
                    items(attendanceList) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Date: ${item.date}")
                                Text("Time: ${item.time}")
                                Text("Status: ${item.status}")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
