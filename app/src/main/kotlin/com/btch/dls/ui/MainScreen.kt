package com.btch.dls.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.btch.dls.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import io.btch.downloader.BtchDownloader
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import android.content.ClipboardManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clipboard
import compose.icons.fontawesomeicons.solid.Moon
import compose.icons.fontawesomeicons.solid.Sun
import compose.icons.fontawesomeicons.solid.Times
import compose.icons.fontawesomeicons.solid.Adjust
import compose.icons.fontawesomeicons.solid.Download
import compose.icons.fontawesomeicons.solid.Search

private val platforms = listOf(
    "ttdl"              to "TikTok",
    "igdl"              to "Instagram",
    "twitter"           to "Twitter / X",
    "youtube"           to "YouTube",
    "fbdown"            to "Facebook",
    "capcut"            to "CapCut",
    "gdrive"            to "Google Drive",
    "pinterest"         to "Pinterest",
    "xiaohongshu"       to "Xiaohongshu",
    "xiaohongshuProfile" to "Xiaohongshu Profile",
    "douyin"            to "Douyin",
    "snackvideo"        to "SnackVideo",
    "cocofun"           to "Cocofun",
    "spotify"           to "Spotify",
    "yts"               to "YouTube Search",
    "soundcloud"        to "SoundCloud",
    "threads"           to "Threads",
    "kuaishou"          to "Kuaishou",
    "mediafire"         to "MediaFire",
)

private val metaKeys = setOf("status", "developer", "message", "note", "code", "creator", "success", "all", "res_data", "author")
private val thumbnailKeys = setOf("thumbnail", "thumb", "cover", "image", "image_url", "coverUrl", "thumbnail_url")
private val json = Json { ignoreUnknownKeys = true }

private val LightBlueWhite = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF42A5F5),
    onSecondary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF616161),
)

private val DarkBlue = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF0D47A1),
    background = Color(0xFF0F1117),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1A1D26),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF252830),
    onSurfaceVariant = Color(0xFFBDBDBD),
)

private fun isUrl(s: String) = s.startsWith("http://") || s.startsWith("https://")

private fun JsonObject.withoutMeta(): List<Map.Entry<String, JsonElement>> =
    entries.filter { it.key !in metaKeys }


private fun JsonElement?.asString(): String? =
    if (this is JsonPrimitive && this.isString) this.content else null

private val platformPatterns = listOf(
    Regex("""tiktok\.(com|v)""") to "ttdl",
    Regex("""douyin\.com|iesdouyin\.com""") to "douyin",
    Regex("""instagram\.com|ig\.me""") to "igdl",
    Regex("""twitter\.com|x\.com|t\.co""") to "twitter",
    Regex("""youtube\.com|youtu\.be""") to "youtube",
    Regex("""facebook\.com|fb\.watch|fb\.gg|fb\.me""") to "fbdown",
    Regex("""capcut\.(com|net)""") to "capcut",
    Regex("""drive\.google\.com""") to "gdrive",
    Regex("""pinterest\.com|pin\.it""") to "pinterest",
    Regex("""xiaohongshu\.com|xhslink\.com""") to { url: String, path: String ->
        if ("/user/" in path || "/profile/" in path) "xiaohongshuProfile" else "xiaohongshu"
    },
    Regex("""kuaishou(app)?\.com""") to "kuaishou",
    Regex("""snackvideo\.com|sck\.io""") to "snackvideo",
    Regex("""icocofun\.com|cocofun\.com""") to "cocofun",
    Regex("""spotify\.com|spotify\.link""") to "spotify",
    Regex("""soundcloud\.com""") to "soundcloud",
    Regex("""threads\.net""") to "threads",
    Regex("""mediafire\.com""") to "mediafire",
)

private fun autoDetectPlatform(url: String): String? {
    if (url.isBlank()) return null
    val lowerUrl = url.lowercase()
    val path = try { android.net.Uri.parse(url).path?.lowercase() ?: "" } catch (e: Exception) { "" }
    
    for ((pattern, platform) in platformPatterns) {
        if (pattern.containsMatchIn(lowerUrl)) {
            return if (platform is Function2<*, *, *>) {
                @Suppress("UNCHECKED_CAST")
                (platform as (String, String) -> String)(lowerUrl, path)
            } else {
                platform as String
            }
        }
    }
    return null
}

private fun JsonElement?.asArray(): JsonArray? = this as? JsonArray
private fun JsonElement?.asObject(): JsonObject? = this as? JsonObject
private fun extractThumbnail(obj: JsonObject): String? {
    val payload = obj["result"]?.asObject() ?: obj["data"]?.asObject() ?: obj
    for (key in thumbnailKeys) {
        val el = payload[key]
        if (el is JsonPrimitive && el.isString && isUrl(el.content)) return el.content
    }
    return null
}

private fun downloadExt(url: String): String = when {
    ".mp4" in url -> "mp4"
    ".mp3" in url -> "mp3"
    ".jpg" in url || ".jpeg" in url || ".webp" in url -> "jpg"
    ".png" in url -> "png"
    ".mov" in url -> "mov"
    ".webm" in url -> "webm"
    else -> "mp4"
}
private fun saveFile(context: Context, url: String): Long {
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val name = "btch_${System.currentTimeMillis()}.${downloadExt(url)}"
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(name)
        .setDescription("Downloading...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
    return dm.enqueue(request)
}

private fun mimeOf(url: String): String = when (url.substringAfterLast(".").substringBefore("?").lowercase()) {
    "mp4", "mov", "webm", "avi" -> "video/mp4"
    "mp3", "aac", "m4a", "wav", "ogg" -> "audio/mpeg"
    "jpg", "jpeg", "png", "webp" -> "image/jpeg"
    else -> "video/mp4"
}


@Composable
private fun MediaDownloadRow(label: String, url: String, onDownload: () -> Long) {
    val context = LocalContext.current
    var downloadId by remember { mutableStateOf<Long?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(downloadId) {
        val id = downloadId ?: return@LaunchedEffect
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        isDownloading = true
        var downloading = true
        while (downloading) {
            val cursor = dm.query(DownloadManager.Query().setFilterById(id))
            if (cursor != null && cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusIndex)
                val downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                
                if (downloadedIndex != -1 && totalIndex != -1) {
                    val downloaded = cursor.getLong(downloadedIndex)
                    val total = cursor.getLong(totalIndex)
                    if (total > 0) {
                        progress = downloaded.toFloat() / total.toFloat()
                    }
                }
                
                if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                    downloading = false
                    isDownloading = false
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        progress = 1f
                        isCompleted = true
                    }
                }
            } else {
                downloading = false
                isDownloading = false
            }
            cursor?.close()
            if (downloading) kotlinx.coroutines.delay(200)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                if (isDownloading) {
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = { downloadId = onDownload() },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) { 
                        Text(if (isCompleted) "Done" else "Download", style = MaterialTheme.typography.labelMedium) 
                    }
                }
            }
            if (isDownloading || isCompleted) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ContentField(key: String, value: JsonElement, context: Context) {
    if (key in metaKeys) return
    when (value) {
        is JsonPrimitive -> when {
            value.isString -> {
                val s = value.content
                if (isUrl(s)) MediaDownloadRow(label = key, url = s, onDownload = { saveFile(context, s) })
                else LabeledTextRow(key = key, value = s)
            }
            value.booleanOrNull != null -> {} // skip booleans
            else -> {} // skip numbers
        }
        is JsonArray -> {
            if (value.isEmpty()) return
            val first = value.firstOrNull()
            if (first is JsonPrimitive && first.isString && isUrl(first.content)) {
                value.forEachIndexed { i, el ->
                    val u = el.jsonPrimitive.content
                    val label = if (value.size > 1) "$key ${i + 1}" else key
                    MediaDownloadRow(label = label, url = u, onDownload = { saveFile(context, u) })
                }
            }
        }
        is JsonObject -> {} // skip — nested result/metadata objects
        is JsonNull -> {}
    }
}

@Composable
private fun LabeledTextRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            key, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.7f),
            maxLines = 3, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ThumbnailCard(url: String?) {
    if (url == null || !isUrl(url)) return
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = url, contentDescription = "Thumbnail",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun PlatformSpecificContent(platform: String, element: JsonObject, context: Context, onYtDownload: (String) -> Unit = {}) {
    val payload = element["result"]?.asObject() ?: element["data"]?.asObject() ?: element["res_data"]?.asObject() ?: element

    @Composable
    fun RenderRow(label: String, url: String?) {
        if (url != null && isUrl(url)) {
            MediaDownloadRow(label, url, { saveFile(context, url) })
        }
    }

    @Composable
    fun RenderText(label: String, value: JsonElement?) {
        val text = when (value) {
            is JsonPrimitive -> value.content
            is JsonArray -> value.mapNotNull { if (it is JsonPrimitive) it.content else null }.joinToString(", ")
            else -> null
        }
        if (!text.isNullOrBlank() && text != "null") {
            LabeledTextRow(label, text)
        }
    }

    when (platform) {
        "tiktok" -> {
            RenderText("Title", payload["title"])
            RenderText("Creator", payload["author"]?.asObject()?.get("nickname"))
            RenderText("Creator ID", payload["author"]?.asObject()?.get("unique_id"))
            RenderText("Region", payload["region"])
            RenderText("Plays", payload["play"])
            RenderText("Duration", payload["duration"])
            RenderText("Music Title", payload["music_info"]?.asObject()?.get("title"))
            RenderText("Music Author", payload["music_info"]?.asObject()?.get("author"))
            RenderText("Music Album", payload["music_info"]?.asObject()?.get("album"))
            Spacer(Modifier.height(8.dp))
            RenderRow("Video (HD)", payload["hdplay"]?.asString())
            RenderRow("Audio", payload["music"]?.asString())
        }
        "ttdl" -> {
            RenderText("Title", payload["title"])
            Spacer(Modifier.height(8.dp))
            payload["video"]?.asArray()?.forEachIndexed { i, el -> RenderRow("Video ${i+1}", el.asString()) }
            payload["audio"]?.asArray()?.forEachIndexed { i, el -> RenderRow("Audio ${i+1}", el.asString()) }
        }
        "igdl" -> {
            payload["url"]?.let { RenderRow("Media", it.asString()) }
            payload.entries.forEach { (_, v) ->
                if (v is JsonArray) {
                    v.forEachIndexed { i, el ->
                        val u = el.asObject()?.get("url")?.asString() ?: el.asString()
                        RenderRow("Media ${i+1}", u)
                    }
                }
            }
        }
        "twitter" -> {
            RenderText("Title", payload["title"])
            Spacer(Modifier.height(8.dp))
            payload["url"]?.asArray()?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                RenderRow("HD Video ${i+1}", obj["hd"]?.asString())
                RenderRow("SD Video ${i+1}", obj["sd"]?.asString())
            }
        }
        "youtube" -> {
            RenderText("Title", payload["title"])
            RenderText("Author", payload["author"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Video (MP4)", payload["mp4"]?.asString())
            RenderRow("Audio (MP3)", payload["mp3"]?.asString())
        }
        "fbdown" -> {
            RenderRow("HD Video", payload["HD"]?.asString())
            RenderRow("Normal Video", payload["Normal_video"]?.asString())
        }
        "mediafire" -> {
            RenderText("Filename", payload["filename"])
            RenderText("Filesize", payload["filesize"])
            RenderText("Upload Date", payload["upload_date"])
            RenderText("MIME Type", payload["mimetype"])
            Spacer(Modifier.height(8.dp))
            val filename = payload["filename"]?.asString() ?: "MediaFire File"
            RenderRow(filename, payload["url"]?.asString())
        }
        "capcut" -> {
            RenderText("Title", payload["title"])
            RenderText("Author", payload["authorName"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Original Video", payload["originalVideoUrl"]?.asString())
        }
        "aio" -> {
            RenderText("Title", payload["title"])
            RenderText("Source", payload["source"])
            Spacer(Modifier.height(8.dp))
            payload["medias"]?.asArray()?.forEach { el ->
                val obj = el.asObject() ?: return@forEach
                val type = obj["type"]?.asString()?.uppercase() ?: "MEDIA"
                val quality = obj["q_text"]?.asString() ?: ""
                RenderRow("$type $quality".trim(), obj["url"]?.asString())
            }
        }
        "pinterest" -> {
            val res = payload["result"]?.asObject() ?: payload
            RenderText("Username", res["user"]?.asObject()?.get("username"))
            RenderText("Description", res["description"])
            Spacer(Modifier.height(8.dp))
            val isVideo = res["is_video"]?.let { if (it is JsonPrimitive) it.booleanOrNull else null } == true
            if (isVideo) {
                RenderRow("Video", res["video_url"]?.asString())
            } else {
                RenderRow("Image", res["image"]?.asString())
            }
        }
        "xiaohongshu", "rednote" -> {
            RenderText("Title", payload["title"])
            RenderText("Description", payload["desc"])
            RenderText("Duration", payload["duration"])
            RenderText("Keywords", payload["keywords"])
            payload["author"]?.asObject()?.let {
                RenderText("Author", it["nickname"])
            }
            payload["engagement"]?.asObject()?.let {
                RenderText("Likes", it["likes"])
                RenderText("Comments", it["comments"])
                RenderText("Shares", it["shares"])
                RenderText("Collects", it["collects"])
            }
            Spacer(Modifier.height(8.dp))
            payload["downloads"]?.asArray()?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                val q = obj["quality"]?.asString() ?: "Video ${i+1}"
                RenderRow(q, obj["url"]?.asString())
            }
            payload["images"]?.asArray()?.forEachIndexed { i, img ->
                RenderRow("Image ${i+1}", img.asString())
            }
        }
        "xiaohongshuProfile", "rednoteProfile" -> {
            val user = payload["user"]?.asObject()
            if (user != null) {
                RenderText("Nickname", user["nickname"])
                RenderText("Red ID", user["redId"])
                RenderText("Bio", user["bio"])
                RenderText("IP Location", user["ipLocation"])
            }
            val stats = payload["stats"]?.asObject()
            if (stats != null) {
                RenderText("Notes", stats["notes"])
            }
            payload["notes"]?.asArray()?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                RenderText("Note ${i+1} Title", obj["title"])
                RenderText("Note ${i+1} Likes", obj["likes"])
                RenderRow("Note ${i+1} Cover", obj["cover"]?.asString())
            }
        }
        "douyin" -> {
            RenderText("Title", payload["title"] ?: payload["data"]?.asObject()?.get("title"))
            Spacer(Modifier.height(8.dp))
            val links = payload["links"]?.asArray() ?: payload["data"]?.asObject()?.get("links")?.asArray()
            links?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                val q = obj["quality"]?.asString() ?: "Video ${i+1}"
                RenderRow(q, obj["url"]?.asString())
            }
        }
        "spotify" -> {
            val data = payload["res_data"]?.asObject() ?: payload
            RenderText("Title", data["title"])
            RenderText("Duration", data["duration"])
            Spacer(Modifier.height(8.dp))
            data["formats"]?.asArray()?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                val q = obj["quality"]?.asString() ?: ""
                val ext = obj["ext"]?.asString() ?: ""
                val label = "Audio $q $ext".trim()
                RenderRow(label.ifEmpty { "Audio ${i+1}" }, obj["url"]?.asString())
            }
        }
        "threads" -> {
            RenderText("Type", payload["type"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Video", payload["video"]?.asString())
            RenderRow("Image", payload["image"]?.asString())
        }
        "kuaishou", "snackvideo" -> {
            RenderText("Title", payload["title"])
            RenderText("Upload Date", payload["uploadDate"])
            payload["creator"]?.asObject()?.let { RenderText("Creator", it["name"]) }
            RenderText("Username", payload["username"])
            RenderText("Author", payload["author"])
            RenderText("Width", payload["width"])
            RenderText("Height", payload["height"])
            RenderText("Description", payload["description"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Video", payload["videoUrl"]?.asString())
        }
        "cocofun" -> {
            RenderText("Topic", payload["topic"])
            RenderText("Caption", payload["caption"])
            Spacer(Modifier.height(8.dp))
            RenderRow("No Watermark", payload["no_watermark"]?.asString())
            RenderRow("Watermark", payload["watermark"]?.asString())
        }
        "soundcloud" -> {
            RenderText("Title", payload["title"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Audio", payload["audio"]?.asString())
            RenderRow("Download MP3", payload["downloadMp3"]?.asString())
            RenderRow("Artwork", payload["downloadArtwork"]?.asString())
        }
        "gdrive" -> {
            val data = payload["data"]?.asObject() ?: payload
            RenderText("Filename", data["filename"])
            RenderText("Filesize", data["filesize"])
            Spacer(Modifier.height(8.dp))
            RenderRow("Download", data["downloadUrl"]?.asString())
        }
        "yts" -> {
            payload["all"]?.asArray()?.forEachIndexed { i, el ->
                val obj = el.asObject() ?: return@forEachIndexed
                val title = obj["title"]?.asString() ?: "Result ${i+1}"
                RenderText("Result ${i+1}", JsonPrimitive(title))
                val thumb = obj["thumbnail"]?.asString()
                if (thumb != null && isUrl(thumb)) {
                    Spacer(Modifier.height(8.dp))
                    ThumbnailCard(thumb)
                    Spacer(Modifier.height(8.dp))
                }
                RenderText("Uploaded", obj["ago"])
                RenderText("Views", obj["views"])
                RenderText("Author", obj["author"]?.asObject()?.get("name"))
                RenderText("Duration", obj["timestamp"])
                val ytUrl = obj["url"]?.asString()
                if (ytUrl != null) {
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { onYtDownload(ytUrl) },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = FontAwesomeIcons.Solid.Download, contentDescription = "Get Links", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Get Download Links")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val api = remember { BtchDownloader() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedPlatform by remember { mutableStateOf(platforms.first().first) }
    var url by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isDarkMode by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val scheme = if (isDarkMode) DarkBlue else LightBlueWhite

    MaterialTheme(colorScheme = scheme) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        val gradientBrush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            "Media Downloader",
                            style = MaterialTheme.typography.titleLarge.copy(brush = gradientBrush, fontWeight = FontWeight.Bold)
                        )
                    },
                    actions = {
                        val rotation by animateFloatAsState(targetValue = if (isDarkMode) 180f else 0f, label = "dark_mode_rotation", animationSpec = tween(500))
                        IconButton(onClick = { isDarkMode = !isDarkMode }) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Adjust,
                                contentDescription = "Toggle dark mode",
                                modifier = Modifier.rotate(rotation).size(20.dp)
                            )
                        }
                    }
                )
            }
        ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                // Input card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Platform", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = platforms.find { it.first == selectedPlatform }?.second ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.labelSmall,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    platforms.forEach { (key, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedPlatform = key
                                                result = null
                                                error = null
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = url, onValueChange = { 
                                    url = it; result = null; error = null
                                    autoDetectPlatform(it)?.let { detected -> selectedPlatform = detected }
                                },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                placeholder = { Text(if (selectedPlatform == "yts") "Search for videos, music, tutorials..." else "Paste link here...") },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = clipboard.primaryClip
                                        if (clip != null && clip.itemCount > 0) {
                                            url = clip.getItemAt(0).text.toString()
                                            autoDetectPlatform(url)?.let { detected -> selectedPlatform = detected }
                                            result = null
                                            error = null
                                        }
                                    }) {
                                        Icon(imageVector = FontAwesomeIcons.Solid.Clipboard, contentDescription = "Paste from clipboard", modifier = Modifier.size(20.dp))
                                    }
                                },
                                trailingIcon = if (url.isNotEmpty()) {
                                    {
                                        IconButton(onClick = { url = ""; result = null; error = null }) {
                                            Icon(imageVector = FontAwesomeIcons.Solid.Times, contentDescription = "Clear", modifier = Modifier.size(20.dp))
                                        }
                                    }
                                } else null
                            )
                            Spacer(Modifier.height(12.dp))
                            val isSearch = selectedPlatform == "yts"
                            Button(
                                onClick = {
                                    if (url.isBlank()) return@Button
                                    loading = true; result = null; error = null
                                    scope.launch {
                                        try { result = api.raw(selectedPlatform, url) }
                                        catch (e: Exception) { error = e.message ?: "Failed to fetch data" }
                                        finally { loading = false }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                enabled = !loading && (isSearch && url.isNotBlank() || isUrl(url.trim())),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = if (isSearch) FontAwesomeIcons.Solid.Search else FontAwesomeIcons.Solid.Download,
                                    contentDescription = if (isSearch) "Search" else "Download",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (isSearch) "Search" else "Download")
                            }
                        }
                    }
                }

                if (loading) {
                    item {
                        val transition = rememberInfiniteTransition()
                        val alpha by transition.animateFloat(
                            initialValue = 0.3f, targetValue = 0.8f,
                            animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
                            label = "skeleton_alpha"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().alpha(alpha).background(Color.Gray.copy(alpha = 0.3f)))
                                }
                            }
                        }
                    }
                }

                // Error
                error?.let { msg ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("\u26A0", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(8.dp))
                                Text(msg, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Result section
                result?.let { rawJson ->
                    val element = try { json.parseToJsonElement(rawJson) } catch (_: Exception) { null }
                    when (element) {
                        is JsonObject -> {
                            // Thumbnail
                            item(key = "thumb") { ThumbnailCard(extractThumbnail(element)) }
                            // Content
                            item(key = "content") {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        PlatformSpecificContent(selectedPlatform, element, context) { ytUrl ->
                                            url = ytUrl
                                            selectedPlatform = "youtube"
                                            result = null
                                            error = null
                                            loading = true
                                            scope.launch {
                                                try { result = api.raw("youtube", ytUrl) }
                                                catch (e: Exception) { error = e.message ?: "Failed to fetch data" }
                                                finally { loading = false }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is JsonArray -> {
                            element.forEachIndexed { idx, el ->
                                item(key = "ig_result_$idx") {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            val obj = el.asObject()
                                            if (obj != null) {
                                                ThumbnailCard(extractThumbnail(obj))
                                                val url = obj["url"]?.asString()
                                                if (url != null && isUrl(url)) {
                                                    Spacer(Modifier.height(8.dp))
                                                    MediaDownloadRow("Media ${idx + 1}", url, { saveFile(context, url) })
                                                }
                                                val entries = obj.entries.filter { (k, _) -> k !in metaKeys && k != "url" }
                                                if (entries.isNotEmpty()) {
                                                    Spacer(Modifier.height(8.dp))
                                                    entries.forEach { (k, v) -> ContentField(k, v, context) }
                                                }
                                            } else {
                                                val url = el.asString()
                                                if (url != null && isUrl(url)) {
                                                    MediaDownloadRow("Media ${idx + 1}", url, { saveFile(context, url) })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is JsonPrimitive -> {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("\u26A0", style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.width(8.dp))
                                        Text(element.content, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }

                // How to use
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("How to Use", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            listOf(
                                "1. Open TikTok / Instagram / YouTube or any supported app",
                                "2. Copy the video link you want to download",
                                "3. Select the platform from the dropdown",
                                "4. Paste the link & tap Download",
                                "5. Tap Download on each media item that appears",
                                "Files will be saved to your Downloads folder"
                            ).forEach {
                                Text("\u2022 $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 1.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Platform: TikTok, Instagram, Twitter/X, YouTube, Facebook, CapCut, Google Drive, Pinterest, Xiaohongshu, Douyin, SnackVideo, Cocofun, Spotify, SoundCloud, Threads, Kuaishou, MediaFire",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Footer
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Text("© Powered by ", style = MaterialTheme.typography.bodySmall)
                            val uriHandler = LocalUriHandler.current
                            Text(
                                "btch-downloader",
                                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { uriHandler.openUri("https://tio.is-a.dev") }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
