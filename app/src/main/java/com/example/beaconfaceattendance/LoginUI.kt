package com.example.beaconfaceattendance

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.delay
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

// 🎨 COLORS
val PrimaryBlue = Color(0xFF4A90E2)
val LightBlue = Color(0xFF7BAEF5)
val Background = Color(0xFFF4F7FC)

// ---------------- SUBJECT FUNCTION ----------------
fun getCurrentSubject(): String {
    val cal = java.util.Calendar.getInstance()
    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = cal.get(java.util.Calendar.MINUTE)

    val time = hour * 60 + minute

    return when (time) {
        in (9 * 60)..(10 * 60 - 1) -> "Math"
        in (10 * 60)..(11 * 60 - 1) -> "Physics"
        in (11 * 60)..(12 * 60 - 1) -> "Chemistry"
        in (13 * 60)..(14 * 60 - 1) -> "Biology"
        else -> "No Class"
    }
}

// ---------------- NAVIGATION ----------------

@Composable
fun AppNavigator() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        DashboardScreen { isLoggedIn = false }
    } else {
        ModernLoginScreen { isLoggedIn = true }
    }
}

// ---------------- LOGIN ----------------

@Composable
fun ModernLoginScreen(onLoginSuccess: () -> Unit) {

    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var openCamera by remember { mutableStateOf(false) }

    if (openCamera) {
        FaceVerificationScreen(
            isRegistering = true,
            onDone = {
                openCamera = false
                onLoginSuccess()
            }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryBlue, LightBlue)))
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Beacon", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("Face Attendance", color = Color.White.copy(0.8f))

            Spacer(Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {

                    OutlinedTextField(email, { email = it }, label = { Text("Email") })
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        password,
                        { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (!isLogin) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            confirmPassword,
                            { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                    if (error.isNotEmpty()) {
                        Text(error, color = Color.Red)
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val auth = FirebaseAuth.getInstance()

                            if (isLogin) {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { onLoginSuccess() }
                                    .addOnFailureListener { error = "Login failed" }
                            } else {
                                if (password != confirmPassword) {
                                    error = "Passwords do not match"
                                    return@Button
                                }

                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { openCamera = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLogin) "Login" else "Register")
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        if (isLogin) "Switch to Register" else "Switch to Login",
                        modifier = Modifier.clickable { isLogin = !isLogin }
                    )
                }
            }
        }
    }
}

// ---------------- DASHBOARD ----------------

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val localContext = LocalContext.current
    var openCamera by remember { mutableStateOf(false) }

    if (openCamera) {
        FaceVerificationScreen(false) { openCamera = false }
        return
    }

    val email = FirebaseAuth.getInstance().currentUser?.email ?: "User"

    Column(Modifier.fillMaxSize().background(Background)) {

        Box(
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Brush.verticalGradient(listOf(PrimaryBlue, LightBlue)))
        ) {

            Column(Modifier.padding(20.dp)) {

                Spacer(Modifier.height(40.dp))

                Text("Welcome", color = Color.White)
                Text(email, color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Logout", color = PrimaryBlue)
                }
            }
        }

        val context = LocalContext.current

        Button(
            onClick = {

                val beaconHelper = BeaconHelper(context)

                beaconHelper.startScanning { found ->

                    if (found) {
                        android.widget.Toast.makeText(
                            context,
                            "Inside classroom ✅",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        openCamera = true

                    } else {
                        android.widget.Toast.makeText(
                            context,
                            "Move closer ❌",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Start Face Verification 📷")
        }


        AttendanceHistoryScreen()
    }
}

// ---------------- FACENET ----------------


fun calculateDistance(a: FloatArray, b: FloatArray): Float {
    var sum = 0f
    for (i in a.indices) {
        val d = a[i] - b[i]
        sum += d * d
    }
    return kotlin.math.sqrt(sum)
}

// ---------------- FACE DETECTOR ----------------

fun getFaceDetector(): FaceDetector {
    return FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )
}

// ---------------- CAMERA ----------------

@OptIn(ExperimentalGetImage::class)
@Composable
fun FaceVerificationScreen(
    isRegistering: Boolean,
    onDone: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var verified by remember { mutableStateOf(false) }
    var attendanceMarked by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Align your face") }

    // ✅ RESET every time screen opens
    LaunchedEffect(Unit) {
        attendanceMarked = false
    }

    Box(Modifier.fillMaxSize()) {

        AndroidView(factory = { ctx ->

            val previewView = PreviewView(ctx)
            val faceNet = FaceNetHelper(ctx)
            val detector = getFaceDetector()
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()

            val providerFuture = ProcessCameraProvider.getInstance(ctx)

            providerFuture.addListener({

                val provider = providerFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analyzer = ImageAnalysis.Builder().build()

                analyzer.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->

                    val mediaImage = imageProxy.image

                    if (mediaImage != null) {

                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        detector.process(image)
                            .addOnSuccessListener { faces ->

                                if (faces.isNotEmpty()) {

                                    val bitmap = previewView.bitmap ?: return@addOnSuccessListener
                                    val embedding = faceNet.getEmbedding(bitmap)
                                    val email = auth.currentUser?.email ?: return@addOnSuccessListener

                                    // 🔹 REGISTER
                                    if (isRegistering) {
                                        db.collection("users")
                                            .document(email)
                                            .set(mapOf("embedding" to embedding.toList()))

                                        verified = true
                                        statusText = "✅ Registered"
                                        return@addOnSuccessListener
                                    }

                                    // 🔹 VERIFY
                                    db.collection("users")
                                        .document(email)
                                        .get()
                                        .addOnSuccessListener { doc ->

                                            val saved = doc.get("embedding") as? List<Double>
                                                ?: return@addOnSuccessListener

                                            val savedEmb = saved.map { it.toFloat() }.toFloatArray()

                                            val distance = calculateDistance(embedding, savedEmb)

                                            if (distance < 1.0f && !attendanceMarked) {

                                                attendanceMarked = true
                                                val subject = getCurrentSubject()

                                                db.collection("attendance")
                                                    .whereEqualTo("email", email)
                                                    .whereEqualTo("subject", subject)
                                                    .get()
                                                    .addOnSuccessListener { docs ->

                                                        if (docs.isEmpty) {
                                                            db.collection("attendance").add(
                                                                mapOf(
                                                                    "email" to email,
                                                                    "time" to System.currentTimeMillis(),
                                                                    "subject" to subject
                                                                )
                                                            )
                                                            statusText = "✅ Attendance Marked"
                                                        } else {
                                                            statusText = "⚠️ Already Marked"
                                                        }

                                                        verified = true
                                                    }

                                            } else if (!attendanceMarked) {
                                                statusText = "❌ Not Matched"
                                            }
                                        }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }

                    } else {
                        imageProxy.close()
                    }
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    analyzer
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView

        }, modifier = Modifier.fillMaxSize())

        // FRAME
        Box(
            Modifier
                .size(260.dp)
                .align(Alignment.Center)
                .border(3.dp, Color.White, RoundedCornerShape(24.dp))
        )

        // STATUS
        Text(
            text = statusText,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter).padding(40.dp)
        )
    }

    if (verified) {
        LaunchedEffect(Unit) {
            delay(1500)
            onDone()
        }
    }
}
// ---------------- HISTORY ----------------

@Composable
fun AttendanceHistoryScreen() {
    val db = FirebaseFirestore.getInstance()
    val email = FirebaseAuth.getInstance().currentUser?.email

    var list by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        db.collection("attendance")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {

                list = it.map { doc ->
                    val time = doc.getLong("time") ?: 0
                    val subject = doc.getString("subject") ?: "Unknown"

                    Pair(
                        time,
                        "$subject - " + java.text.SimpleDateFormat(
                            "dd MMM, hh:mm a"
                        ).format(java.util.Date(time))
                    )
                }
                    .sortedByDescending { it.first }
                    .map { it.second }
            }
    }

    Column(Modifier.padding(16.dp)) {
        list.forEach {
            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(it, Modifier.padding(18.dp))
            }
        }
    }
}
