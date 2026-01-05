package com.shreyapoojary.smartfaceattendance.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.shreyapoojary.smartfaceattendance.ml.FaceMatcher
import com.shreyapoojary.smartfaceattendance.ml.FaceNetModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FaceMatchScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        Text("Camera permission required")
        return
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        /* ================= CAMERA PREVIEW ================= */

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder().build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageCapture
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        /* ================= VERIFY BUTTON ================= */

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            onClick = {
                val capture = imageCapture ?: return@Button

                val photoFile = File(
                    context.cacheDir,
                    "match_${System.currentTimeMillis()}.jpg"
                )

                val outputOptions =
                    ImageCapture.OutputFileOptions.Builder(photoFile).build()

                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(
                            output: ImageCapture.OutputFileResults
                        ) {

                            detectFace(context, photoFile) { faceFound ->
                                if (!faceFound) {
                                    Toast.makeText(
                                        context,
                                        "No face detected",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@detectFace
                                }

                                /* -------- LIVE EMBEDDING -------- */

                                val bitmap =
                                    BitmapFactory.decodeFile(photoFile.absolutePath)

                                val faceNet = FaceNetModel(context)
                                val liveEmbedding =
                                    faceNet.getEmbedding(bitmap)

                                val uid =
                                    FirebaseAuth.getInstance().currentUser?.uid
                                        ?: return@detectFace

                                val db = FirebaseFirestore.getInstance()

                                /* -------- STORED EMBEDDING -------- */

                                db.collection("face_encodings")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { encodingDoc ->

                                        val stored =
                                            encodingDoc.get("embedding") as? List<Double>
                                                ?: return@addOnSuccessListener

                                        val storedEmbedding =
                                            stored.map { it.toFloat() }.toFloatArray()

                                        val matched = FaceMatcher.isMatch(
                                            liveEmbedding,
                                            storedEmbedding
                                        )

                                        if (!matched) {
                                            Toast.makeText(
                                                context,
                                                "❌ FACE NOT MATCHED",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@addOnSuccessListener
                                        }

                                        /* -------- ROLE CHECK -------- */

                                        db.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener { userDoc ->

                                                val role =
                                                    userDoc.getString("role") ?: "student"

                                                if (role != "student") {
                                                    Toast.makeText(
                                                        context,
                                                        "Attendance allowed only for students",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    return@addOnSuccessListener
                                                }

                                                val studentName =
                                                    userDoc.getString("name") ?: "Student"

                                                val today = SimpleDateFormat(
                                                    "yyyy-MM-dd",
                                                    Locale.getDefault()
                                                ).format(Date())

                                                val time = SimpleDateFormat(
                                                    "HH:mm:ss",
                                                    Locale.getDefault()
                                                ).format(Date())

                                                println(
                                                    "Trying to read attendance for UID: $uid on date $today"
                                                )

                                                val attendanceRef = db
                                                    .collection("attendance")
                                                    .document(today)
                                                    .collection("students")
                                                    .document(uid)

                                                /* -------- DUPLICATE CHECK -------- */

                                                attendanceRef.get()
                                                    .addOnSuccessListener { doc ->

                                                        if (doc.exists()) {
                                                            Toast.makeText(
                                                                context,
                                                                "⚠️ Attendance already marked today",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        } else {

                                                            val attendanceData = mapOf(
                                                                "date" to today,
                                                                "time" to time,
                                                                "status" to "Present",
                                                                "timestamp" to System.currentTimeMillis()
                                                            )

                                                            // ✅ GLOBAL (TEACHER)
                                                            db.collection("attendance")
                                                                .document(today)
                                                                .collection("students")
                                                                .document(uid)
                                                                .set(attendanceData)

                                                            // ✅ USER (STUDENT HISTORY)
                                                            db.collection("users")
                                                                .document(uid)
                                                                .collection("attendance")
                                                                .document(today)
                                                                .set(attendanceData)

                                                            Toast.makeText(
                                                                context,
                                                                "✅ Attendance marked for $studentName",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            }
                                    }
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(
                                context,
                                exception.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        ) {
            Text("Verify Face")
        }

        /* ================= BACK BUTTON ================= */

        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

/* ================= FACE DETECTION ================= */

private fun detectFace(
    context: Context,
    imageFile: File,
    onResult: (Boolean) -> Unit
) {
    val image = InputImage.fromFilePath(
        context,
        Uri.fromFile(imageFile)
    )

    val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )

    detector.process(image)
        .addOnSuccessListener { faces ->
            onResult(faces.isNotEmpty())
        }
        .addOnFailureListener {
            onResult(false)
        }
}
