package com.shreyapoojary.smartfaceattendance.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutAppScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Smart Face Attendance",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Text("Version: 1.0")

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Developed By",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Name: Karthik Rai")
        Text("Course: MCA")
        Text("Department: Computer Applications")
        Text("College: Shree Devi Institute Of Technology")

        Spacer(Modifier.height(12.dp))

        Text("Email: karthik9860rai@gmail.com")
        Text("GitHub: github.com/karthikrai")

        Spacer(Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
