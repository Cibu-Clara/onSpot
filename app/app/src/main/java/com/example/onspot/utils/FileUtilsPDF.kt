package com.example.onspot.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import java.io.File

fun openPdf(context: Context, remoteUrl: String, localFileName: String) {
    val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), localFileName)
    val pdfRef = FirebaseStorage.getInstance().getReferenceFromUrl(remoteUrl)

    pdfRef.getFile(localFile).addOnSuccessListener {
        viewPdf(context, localFile)
    }.addOnFailureListener { exception ->
        Toast.makeText(context, "Failed to open PDF: ${exception.message}", Toast.LENGTH_LONG).show()
        Log.e("OpenPDF", "Failed to open PDF", exception)
    }
}

private fun viewPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_LONG).show()
        Log.e("OpenPDF", "No application available to view PDF", e)
    }
}

