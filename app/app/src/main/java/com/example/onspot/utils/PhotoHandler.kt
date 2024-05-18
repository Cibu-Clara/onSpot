package com.example.onspot.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PhotoHandler(private val context: Context) {
    var photoUri: Uri? = null

    fun takePhoto(launcher: ActivityResultLauncher<Uri>) {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "picture_${System.currentTimeMillis()}.jpg"
        ).apply {
            createNewFile()
        }
        val currentPhotoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        photoUri = currentPhotoUri

        launcher.launch(currentPhotoUri)
    }

    fun openJpg(context: Context, remoteUrl: String, localFileName: String) {
        val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), localFileName)

        if (localFile.exists()) {
            openLocalJpg(context, localFile)
        } else {
            val jpgRef = FirebaseStorage.getInstance().getReferenceFromUrl(remoteUrl)

            jpgRef.getFile(localFile).addOnSuccessListener {
                openLocalJpg(context, localFile)
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to download JPG: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("DownloadJPG", "Failed to download JPG", exception)
            }
        }
    }

    private fun openLocalJpg(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No application available to view JPG", Toast.LENGTH_LONG).show()
            Log.e("OpenJPG", "No application available to view JPG", e)
        }
    }
}
