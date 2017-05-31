package com.floatingmuseum.androidtest.functions.media

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.floatingmuseum.androidtest.R
import com.floatingmuseum.androidtest.base.BaseActivity
import com.orhanobut.logger.Logger
import floatingmuseum.floatingmusic.MusicItem

class MediaActivity : BaseActivity() {
    val musicList = ArrayList<MusicItem>()
    val imageList = ArrayList<ImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        scanMusic()
        scanImage()
    }

    /**
     * 扫描歌曲
     */
    private fun scanMusic() {
        musicList.clear()
        val path = Environment.getExternalStorageDirectory().absolutePath + "/netease/cloudmusic/Music"
        Logger.d("Music信息...path:" + path)
        val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
//        if (cursor == null) {
//            return
//        }
        while (cursor.moveToNext()) {
            // 是否为音乐
            val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC))
            if (isMusic == 0) {
                continue
            }
            // 音乐uri
            val uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            if (!uri.contains(path)) {
                continue
            }
            // ID
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
            // 标题
            val title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))
            // 艺术家
            val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            // 专辑
            val album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))
            // 持续时间
            val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            // 专辑封面id，根据该id可以获得专辑图片uri
            val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            val coverUri = getCoverUri(albumId)
            // 音乐文件名
            val fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))
            // 音乐文件大小
            val fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
            // 发行时间
            val year = cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)))
            val music = MusicItem(id, title, artist, album, duration, uri, albumId, coverUri, fileName, fileSize, year)

            musicList.add(music)
        }
        cursor.close()
        for (item in musicList) {
            Logger.d("Music信息:" + item.toString())
        }
    }

    /**
     * 查询专辑封面图片uri
     */
    fun getCoverUri(albumId: Long): String {
        var uri = null
        val cursor = contentResolver.query(
                Uri.parse("content://media/external/audio/albums/" + albumId),  arrayOf("album_art") , null, null, null)
        if (cursor != null) {
            cursor.moveToNext()
            uri = cursor.getString(0) as Nothing?
            cursor.close()
        }
//        CoverLoader.getInstance().loadThumbnail(uri)
        return uri!!
    }

    private fun scanImage() {
        imageList.clear()
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
        while (cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            val count = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._COUNT))
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
            val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
            val width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
            val height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
            val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
            val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
            val dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
            val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
            val dateTaken = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val picasaID = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.PICASA_ID))
            val orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION))
            val miniThumbMagic = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC))
            val longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE))
            val latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE))
            val isPrivate = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.IS_PRIVATE))
            val description = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION))
            val bucketID = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
            val bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            val image = ImageItem(id,count,title,size,width,height,mimeType,displayName,dateModified,dateAdded,dateTaken,data,picasaID,orientation,miniThumbMagic,longitude,latitude,isPrivate,description,bucketID,bucketDisplayName)
            imageList.add(image)
        }
        cursor.close()
        for (item in imageList) {
            Logger.d("Image信息:" + item.toString())
        }
    }
}
