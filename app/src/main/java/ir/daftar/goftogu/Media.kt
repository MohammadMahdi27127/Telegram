package ir.daftar.goftogu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

private const val TEN_MB = 10_000_000L

suspend fun prepareImage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
    val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } ?: error("عکس خوانده نشد")
    val maxSide = maxOf(bitmap.width, bitmap.height).coerceAtLeast(1)
    val scale = minOf(1f, 1600f / maxSide)
    val scaled = if (scale < 1f) {
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt().coerceAtLeast(1), (bitmap.height * scale).toInt().coerceAtLeast(1), true)
    } else bitmap
    val dest = File(context.filesDir, "photo-${System.currentTimeMillis()}.jpg")
    dest.outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 80, it) }
    if (scaled !== bitmap) scaled.recycle()
    dest.absolutePath
}

suspend fun prepareVideo(context: Context, uri: Uri): String {
    val size = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: Long.MAX_VALUE
    if (size in 1..TEN_MB) {
        val dest = File(context.filesDir, "video-${System.currentTimeMillis()}.mp4")
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } } ?: error("فیلم خوانده نشد")
        }
        return dest.absolutePath
    }
    var height = 720
    var last = ""
    repeat(3) {
        val dest = File(context.filesDir, "video-${System.currentTimeMillis()}-$height.mp4")
        transcode(context, uri, dest, height)
        last = dest.absolutePath
        if (dest.length() <= TEN_MB) return last
        height = (height * 0.62f).toInt().coerceAtLeast(240)
    }
    error("حجم فیلم بعد از فشرده‌سازی هنوز بیشتر از ۱۰ مگابایت است")
}

private suspend fun transcode(context: Context, uri: Uri, dest: File, height: Int) = suspendCancellableCoroutine { cont ->
    val effects = Effects(emptyList<androidx.media3.common.audio.AudioProcessor>(), listOf(Presentation.createForHeight(height)))
    val edited = EditedMediaItem.Builder(MediaItem.fromUri(uri)).setEffects(effects).build()
    val transformer = Transformer.Builder(context)
        .setVideoMimeType(MimeTypes.VIDEO_H264)
        .setAudioMimeType(MimeTypes.AUDIO_AAC)
        .addListener(object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                if (cont.isActive) cont.resume(Unit)
            }

            override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                if (cont.isActive) cont.resumeWithException(exportException)
            }
        })
        .build()
    cont.invokeOnCancellation { runCatching { transformer.cancel() } }
    transformer.start(edited, dest.absolutePath)
}

fun savePersonAvatar(context: Context, uri: Uri, personId: String): String {
    val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } ?: error("عکس خوانده نشد")
    val side = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - side) / 2
    val y = (bitmap.height - side) / 2
    val square = Bitmap.createBitmap(bitmap, x, y, side, side)
    val scaled = Bitmap.createScaledBitmap(square, 512, 512, true)
    val dest = File(context.filesDir, "person-$personId.jpg")
    dest.outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    if (scaled !== square) scaled.recycle()
    if (square !== bitmap) square.recycle()
    return dest.absolutePath
}
