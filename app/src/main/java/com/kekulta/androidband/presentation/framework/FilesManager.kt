package com.kekulta.androidband.presentation.framework

import android.content.Context
import java.io.File

class FilesManager(
    private val context: Context,
) {
    val cacheDir: File = context.cacheDir
    val filesDir: File = context.filesDir
    val audiosDir: File = File("$filesDir/audios/")
        get() {
            if (!field.exists()) {
                field.mkdir()
            }
            return field
        }
    val assetDir: File = File("$cacheDir/assets/")
        get() {
            if (!field.exists()) {
                field.mkdir()
            }
            return field
        }

    fun renameTo(file: File, newName: String): File {
        val newFile = File("${file.parentFile ?: ""}/$newName.wav")
        newFile.delete()
        file.renameTo(newFile)

        return newFile
    }

}