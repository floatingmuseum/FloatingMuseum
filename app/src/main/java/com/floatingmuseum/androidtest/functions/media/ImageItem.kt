package com.floatingmuseum.androidtest.functions.media

import android.renderscript.ScriptIntrinsicYuvToRGB

/**
 * Created by Floatingmuseum on 2017/5/31.
 */
data class ImageItem(val id: Long, val count: Int, val title: String, val size: Long, val height: Int, val width: Int, val mimeType: String, val displayName: String, val dateModified: Long, val dateAdded: Long, val dateTaken: Int, val data: String, val picasaID: String, val orientation: Int, val miniThumbMagic: Int, val longitude: Double, val latitude: Double, val isPrivate: Int, val description: String, val bucketID: String, val bucketDisplayName: String)