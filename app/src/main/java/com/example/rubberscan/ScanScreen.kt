package com.example.rubberscan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


// ── Scan Screen ────────────────────────────────────────────
@Composable
fun ScanScreen(
    onBack: () -> Unit = {},
    onCapture: () -> Unit = {},
    temperature: Float? = null,       // ← from DHT22 via ESP32 BLE
    humidity: Float? = null,          // ← null means "no reading yet"
    isSensorConnected: Boolean = false

) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

// format for display, with a fallback when nothing has arrived yet
    val tempText     = temperature?.let { "%.1f°C".format(it) } ?: "--°C"
    val humidityText = humidity?.let { "${it.toInt()}% RH" } ?: "--% RH"

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        CameraPermissionDenied(onBack = onBack, onRequest = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        })
        return
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    var isCapturing  by remember { mutableStateOf(false) }
    var isFlashOn    by remember { mutableStateOf(false) }
    var camera       by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── Live Camera Preview ──────────────────────────────
        CameraPreview(
            modifier       = Modifier.fillMaxSize(),
            imageCapture   = imageCapture,
            lifecycleOwner = lifecycleOwner,
            onCameraReady  = { camera = it }
        )

        // ── UI Overlay ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()   // ← handles status bar + nav bar area
        ) {
            // ── Top Bar ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null,
                        tint = if (isSensorConnected) Color(0xFF4CAF50) else Color(0xFFE57373),
                        modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isSensorConnected) "Sensor Connected" else "Sensor Offline",
                        color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFlashOn) Color.White.copy(alpha = 0.6f)
                            else Color.Black.copy(alpha = 0.4f)
                        )
                        .clickable {
                            isFlashOn = !isFlashOn
                            camera?.cameraControl?.enableTorch(isFlashOn)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color(0xFFFFD600) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── Sensor Panel ──────────────────────────────────
            Box(modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 6 .dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Thermostat, contentDescription = null,
                        tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(tempText, color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp)
                        .background(Color.White.copy(alpha = 0.2f)))
                    Spacer(Modifier.width(12.dp))

                    Icon(Icons.Default.WaterDrop, contentDescription = null,
                        tint = Color(0xFF64B5F6), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(humidityText, color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp)
                        .background(Color.White.copy(alpha = 0.2f)))
                    Spacer(Modifier.width(12.dp))

                    PulsingDot()
                    Spacer(Modifier.width(4.dp))
                    Text("Live", color = Color(0xFF4CAF50), fontSize = 11.sp)
                }
            }

            // ── Leaf Guide Frame ──────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier.size(260.dp, 360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LeafGuideOverlay()
                }

                Spacer(Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Place a rubber leaf inside the guide frame.",
                        color = Color.White, fontSize = 10.sp,
                        fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(1.dp))
                    Text("Hold steady for best results",
                        color = Color(0xFFBDBDBD), fontSize = 11.sp,
                        textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── Capture Button ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(

                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (isCapturing) 0.1f else 0.2f))
                        .clickable(enabled = !isCapturing) {
                            isCapturing = true
                            capturePhoto(
                                context      = context,
                                imageCapture = imageCapture,
                                onSuccess = {
                                    isFlashOn = false
                                    camera?.cameraControl?.enableTorch(false)
                                    onCapture()
                                },
                                onError      = { isCapturing = false }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color  = Color.White,
                            radius = size.minDimension / 2 - 2.dp.toPx(),
                            style  = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                        )
                    }
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(40.dp),
                            color       = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Box(modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White))
                    }
                }
            }
        }
    }
}

// ── CameraX Preview ─────────────────────────────────────────
@Composable
fun CameraPreview(
    modifier       : Modifier,
    imageCapture   : ImageCapture,
    lifecycleOwner : androidx.lifecycle.LifecycleOwner,
    onCameraReady  : (androidx.camera.core.Camera) -> Unit = {}   // ← add this
) {
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                try {
                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(   // ← capture return value
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                    onCameraReady(cam)                          // ← pass it back
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

// ── Capture logic ────────────────────────────────────────────
private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val outputFile    = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    val executor      = Executors.newSingleThreadExecutor()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                ContextCompat.getMainExecutor(context).execute { onSuccess() }
            }
            override fun onError(exc: ImageCaptureException) {
                exc.printStackTrace()
                ContextCompat.getMainExecutor(context).execute { onError() }
            }
        }
    )
}

// ── Permission Denied Screen ─────────────────────────────────
@Composable
private fun CameraPermissionDenied(onBack: () -> Unit, onRequest: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Camera Permission Required",
                color = Color.White, fontSize = 18.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("RubberScan needs camera access to scan rubber leaves.",
                color = Color(0xFFBDBDBD), fontSize = 14.sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Grant Permission", color = Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) {
                Text("Go Back", color = Color(0xFFBDBDBD), fontSize = 14.sp)
            }
        }
    }
}

// ── Pulsing Dot ────────────────────────────────────────────
@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 0.3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(Color(0xFF4CAF50).copy(alpha = alpha)))
}

// ── Leaf Guide Overlay ─────────────────────────────────────
@Composable
fun LeafGuideOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanPosition by infiniteTransition.animateFloat(
        initialValue  = 0.2f,
        targetValue   = 0.8f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w          = size.width
        val h          = size.height
        val bracketLen = 28.dp.toPx()
        val strokeW    = 3.dp.toPx()

        drawLine(Color.White, Offset(0f, bracketLen), Offset(0f, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(0f, 0f), Offset(bracketLen, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w - bracketLen, 0f), Offset(w, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w, 0f), Offset(w, bracketLen), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w, h - bracketLen), Offset(w, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w, h), Offset(w - bracketLen, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(bracketLen, h), Offset(0f, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(0f, h), Offset(0f, h - bracketLen), strokeWidth = strokeW, cap = StrokeCap.Round)

        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        drawOval(
            color   = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size    = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.76f),
            style   = androidx.compose.ui.graphics.drawscope.Stroke(
                width      = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )
        )
        drawLine(
            color       = Color(0xFF4CAF50).copy(alpha = 0.7f),
            start       = Offset(w * 0.06f, h * scanPosition),
            end         = Offset(w * 0.94f, h * scanPosition),
            strokeWidth = 2.dp.toPx(),
            cap         = StrokeCap.Round
        )
    }
}

