package com.shreyapoojary.smartfaceattendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.shreyapoojary.smartfaceattendance.ui.auth.LoginScreen
import com.shreyapoojary.smartfaceattendance.ui.auth.RegisterScreen
import com.shreyapoojary.smartfaceattendance.ui.camera.FaceRegisterScreen
import com.shreyapoojary.smartfaceattendance.ui.camera.FaceMatchScreen
import com.shreyapoojary.smartfaceattendance.ui.home.HomeScreen
import com.shreyapoojary.smartfaceattendance.ui.theme.SmartFaceAttendanceTheme
import com.shreyapoojary.smartfaceattendance.ui.attendance.AttendanceHistoryScreen
import com.shreyapoojary.smartfaceattendance.ui.admin.AdminDashboardScreen
import com.shreyapoojary.smartfaceattendance.ui.about.AboutAppScreen   // ✅ NEW

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartFaceAttendanceTheme {

                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()

                val startDestination =
                    if (auth.currentUser != null) "home" else "login"

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    /* ---------------- LOGIN ---------------- */
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onGoToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    /* ---------------- REGISTER ---------------- */
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    /* ---------------- HOME ---------------- */
                    composable("home") {
                        HomeScreen(
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onRegisterFace = {
                                navController.navigate("face_register")
                            },
                            onVerifyFace = {
                                navController.navigate("face_match")
                            },
                            onViewAttendance = {
                                navController.navigate("attendance_history")
                            },
                            onAdminDashboard = {
                                navController.navigate("admin_dashboard")
                            },
                            onAboutApp = {                    // ✅ NEW
                                navController.navigate("about")
                            }
                        )
                    }

                    /* ---------------- FACE REGISTER ---------------- */
                    composable("face_register") {
                        FaceRegisterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------------- FACE MATCH ---------------- */
                    composable("face_match") {
                        FaceMatchScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------------- STUDENT ATTENDANCE HISTORY ---------------- */
                    composable("attendance_history") {
                        AttendanceHistoryScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------------- TEACHER / ADMIN DASHBOARD ---------------- */
                    composable("admin_dashboard") {
                        AdminDashboardScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------------- ABOUT / DEVELOPER ---------------- */
                    composable("about") {
                        AboutAppScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
