package com.plcoding.androidstorage

import android.graphics.Bitmap
import android.net.Uri

data class SharedStoragePhoto(
    val id: Long,
    val fotoid: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri,
    val compressBitmap: Bitmap? = null
)