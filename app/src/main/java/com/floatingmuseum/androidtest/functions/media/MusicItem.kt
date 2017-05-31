package floatingmuseum.floatingmusic

/**
 * Created by Floatingmuseum on 2017/5/31.
 */
data class MusicItem(val id: Long, val title: String, val artist: String, val album: String, val duration: Long, val uri: String, val albumID: Long, val coverUri: String?, val fileName: String, val fileSize: Long, val year: Int)