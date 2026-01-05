package com.shreyapoojary.smartfaceattendance.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.shreyapoojary.smartfaceattendance.utils.AttendanceExporter
import com.shreyapoojary.smartfaceattendance.utils.ExportAttendanceItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var attendanceList by remember {
        mutableStateOf<List<ExportAttendanceItem>>(emptyList())
    }

    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("attendance")
            .document(today)
            .collection("students")
            .get()
            .addOnSuccessListener { result ->
                attendanceList = result.documents.map {
                    ExportAttendanceItem(
                        name = it.getString("name") ?: "Unknown",
                        time = it.getString("time") ?: "--",
                        status = it.getString("status") ?: "Absent"
                    )
                }
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Teacher Dashboard ($today)",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        when {
            loading -> {
                CircularProgressIndicator()
            }

            attendanceList.isEmpty() -> {
                Text("No attendance marked today")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(attendanceList) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Time: ${item.time}")
                                Text("Status: ${item.status}")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 📊 EXPORT BUTTON
        if (attendanceList.isNotEmpty()) {
            Button(
                onClick = {
                    AttendanceExporter.exportToExcel(
                        context = context,
                        date = today,
                        attendanceList = attendanceList
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Attendance to Excel")
            }

            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
