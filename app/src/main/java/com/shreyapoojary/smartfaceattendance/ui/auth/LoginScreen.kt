package com.shreyapoojary.smartfaceattendance.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = !loading,
            onClick = {

                // ✅ VALIDATION (THIS FIXES THE CRASH)
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        context,
                        "Email and password cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                loading = true

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        loading = false
                        onLoginSuccess()
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        Toast.makeText(
                            context,
                            e.message ?: "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        ) {
            Text(if (loading) "Logging in..." else "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onGoToRegister) {
            Text(" New User? Create new account")
        }
    }
}
