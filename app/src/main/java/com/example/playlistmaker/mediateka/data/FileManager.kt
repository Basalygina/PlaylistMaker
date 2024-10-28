package com.example.playlistmaker.mediateka.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.playlistmaker.config.App.Companion.TAG
import java.io.File
import java.io.FileOutputStream

class FileManager(private val context: Context) {
    fun saveImageToAppStorage(uri: Uri): String {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Playlists")

        // Создаем директорию, если она не существует
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Unable to open input stream for URI: $uri")

            // Декодируем и сжимаем изображение
            BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                file.path // Возвращаем путь к сохраненному файлу
            } ?: throw IllegalArgumentException("Unable to decode bitmap from URI: $uri")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image to app storage: ${e.message}", e)
            throw e
        } finally {
            // Закрываем потоки, если они были открыты
            try {
                outputStream.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing output stream: ${e.message}", e)
            }
        }
    }

    fun getSafeFileName(fileName: String): String {
        val regex = Regex("[\\\\/:*?\"<>|]")
        return fileName.replace(regex, "_")
    }
}