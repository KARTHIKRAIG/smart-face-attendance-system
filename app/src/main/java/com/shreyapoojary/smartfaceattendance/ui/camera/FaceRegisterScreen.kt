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
import com.google.firebase.firestore.SetOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.shreyapoojary.smartfaceattendance.ml.FaceNetModel
import java.io.File

@Composable
fun FaceRegisterScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission required")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Back") }
        }
        return
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 📷 CAMERA PREVIEW
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

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

        // 📸 CAPTURE BUTTON
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            onClick = {
                val capture = imageCapture ?: return@Button

                val photoFile = File(
                    context.cacheDir,
                    "face_${System.currentTimeMillis()}.jpg"
                )

                val outputOptions =
                    ImageCapture.OutputFileOptions.Builder(photoFile).build()

                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(
                            outputFileResults: ImageCapture.OutputFileResults
                        ) {
                            // 🔍 STEP 1: Detect face
                            detectFace(context, photoFile) { faceFound ->

                                if (!faceFound) {
                                    Toast.makeText(
                                        context,
                                        "No face detected. Try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@detectFace
                                }

                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    ?: return@detectFace

                                // 🧠 STEP 2: Decode bitmap
                                val bitmap =
                                    BitmapFactory.decodeFile(photoFile.absolutePath)

                                // 🧠 STEP 3: Generate FaceNet embedding
                                val faceNet = FaceNetModel(context)
                                val embedding = faceNet.getEmbedding(bitmap)

                                val db = FirebaseFirestore.getInstance()

                                // 🧠 STEP 4: Save embedding
                                db.collection("face_encodings")
                                    .document(uid)
                                    .set(
                                        mapOf(
                                            "embedding" to embedding.toList(),
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                    )
                                    .addOnSuccessListener {

                                        // 🧠 STEP 5: Mark faceRegistered
                                        db.collection("users")
                                            .document(uid)
                                            .set(
                                                mapOf("faceRegistered" to true),
                                                SetOptions.merge()
                                            )

                                        Toast.makeText(
                                            context,
                                            "Face registered successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBack()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to save face encoding",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(
                                context,
                                "Capture failed: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        ) {
            Text("Capture Face")
        }

        // 🔙 BACK BUTTON
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

    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()

    val detector = FaceDetection.getClient(options)

    detector.process(image)
        .addOnSuccessListener { faces ->
            onResult(faces.isNotEmpty())
        }
        .addOnFailureListener {
            onResult(false)
        }
}
