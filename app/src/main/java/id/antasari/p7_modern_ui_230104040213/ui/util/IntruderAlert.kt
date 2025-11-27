// app/src/main/java/id/antasari/p7_modern_ui_230104040213/util/IntruderAlert.kt
package id.antasari.p7_modern_ui_230104040213.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

object IntruderAlert {

    // Try CameraX capture; if anything fails, fallback to ACTION_IMAGE_CAPTURE intent.
    fun capturePhoto(activity: FragmentActivity, onSaved: (File?) -> Unit = {}) {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val imageCapture = ImageCapture.Builder().build()

                // create temp file
                val filename = "intruder_${System.currentTimeMillis()}.jpg"
                val outputDir = activity.getExternalFilesDir("intruder_photos") ?: activity.filesDir
                val outFile = File(outputDir, filename)

                val outputOptions = ImageCapture.OutputFileOptions.Builder(outFile).build()
                try {
                    imageCapture.takePicture(
                        outputOptions,
                        Executors.newSingleThreadExecutor(),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                onSaved(outFile)
                            }
                            override fun onError(exception: ImageCaptureException) {
                                // fallback
                                onSaved(null)
                                fallbackCapture(activity, outFile, onSaved)
                            }
                        }
                    )
                    // bind to lifecycle with a basic selector to ensure camera has a target
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(activity, cameraSelector, imageCapture)
                } catch (e: Exception) {
                    onSaved(null)
                    fallbackCapture(activity, outFile, onSaved)
                }
            }, ContextCompat.getMainExecutor(activity))
        } catch (e: Exception) {
            // fallback to intent capture
            val out = File(activity.filesDir, "intruder_${System.currentTimeMillis()}.jpg")
            fallbackCapture(activity, out, onSaved)
        }
    }

    // Fallback: launch camera intent (requires handling result in Activity if you want file)
    private fun fallbackCapture(activity: FragmentActivity, outFile: File, onSaved: (File?) -> Unit) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Note: Using intent without file provider is limited; we'll just show toast and call onSaved(null)
            activity.startActivity(intent)
            Toast.makeText(activity, "Intruder capture (intent) started", Toast.LENGTH_SHORT).show()
            onSaved(null)
        } catch (_: Exception) {
            onSaved(null)
        }
    }
}
