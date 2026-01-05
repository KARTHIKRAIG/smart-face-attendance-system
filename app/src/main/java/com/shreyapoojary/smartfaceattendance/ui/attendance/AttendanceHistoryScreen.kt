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
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    var attendanceList by remember { mutableStateOf<List<AttendanceItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("attendance")
            .get()
            .addOnSuccessListener { datesSnapshot ->

                val tempList = mutableListOf<AttendanceItem>()

                datesSnapshot.documents.forEach { dateDoc ->
                    val date = dateDoc.id

                    db.collection("attendance")
                        .document(date)
                        .collection("students")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { studentDoc ->
                            if (studentDoc.exists()) {
                                tempList.add(
                                    AttendanceItem(
                                        date = date,
                                        time = studentDoc.getString("time") ?: "--",
                                        status = studentDoc.getString("status") ?: "Absent"
                                    )
                                )
                                attendanceList = tempList.sortedByDescending { it.date }
                            }
                        }
                }
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

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (attendanceList.isEmpty()) {
            Text("No attendance records found")
        } else {
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

        Spacer(Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
