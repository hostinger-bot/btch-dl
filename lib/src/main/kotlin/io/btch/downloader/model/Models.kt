package io.btch.downloader.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ── TikTok ────────────────────────────────────────────────────────────────

@Serializable
public data class TikTokResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val title: String? = null,
    public val title_audio: String? = null,
    public val thumbnail: String? = null,
    public val video: List<String>? = null,
    public val audio: List<String>? = null,
)

// ── Instagram ─────────────────────────────────────────────────────────────

@Serializable
public data class InstagramMediaItem(
    val thumbnail: String? = null,
    val url: String? = null,
)

@Serializable
public data class InstagramResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: List<InstagramMediaItem>? = null,
)

// ── Twitter / X ───────────────────────────────────────────────────────────

@Serializable
public data class TwitterResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val title: String? = null,
    public val url: String? = null,
)

// ── YouTube ───────────────────────────────────────────────────────────────

@Serializable
public data class YouTubeResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val title: String? = null,
    public val thumbnail: String? = null,
    public val author: String? = null,
    public val mp3: String? = null,
    public val mp4: String? = null,
)

// ── Facebook ──────────────────────────────────────────────────────────────

@Serializable
public data class FacebookResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val Normal_video: String? = null,
    public val HD: String? = null,
)

// ── MediaFire ─────────────────────────────────────────────────────────────

@Serializable
public data class MediaFireFileInfo(
    val status: Boolean? = null,
    val filename: String? = null,
    val filesize: String? = null,
    val filesizeH: String? = null,
    val type: String? = null,
    val upload_date: String? = null,
    val owner: String? = null,
    val ext: String? = null,
    val mimetype: String? = null,
    val url: String? = null,
)

@Serializable
public data class MediaFireResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: MediaFireFileInfo? = null,
)

// ── CapCut ────────────────────────────────────────────────────────────────

@Serializable
public data class CapCutResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val title: String? = null,
    public val originalVideoUrl: String? = null,
    public val coverUrl: String? = null,
    public val authorName: String? = null,
)

// ── Google Drive ──────────────────────────────────────────────────────────

@Serializable
public data class GoogleDriveFileInfo(
    val status: Boolean? = null,
    val filename: String? = null,
    val filesize: String? = null,
    val downloadUrl: String? = null,
)

@Serializable
public data class GoogleDriveResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: GoogleDriveFileInfo? = null,
)

// ── Pinterest ─────────────────────────────────────────────────────────────

@Serializable
public data class PinterestUploader(
    val username: String? = null,
    val full_name: String? = null,
    val profile_url: String? = null,
    val avatar_url: String? = null,
)

@Serializable
public data class PinterestPin(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val pin_url: String? = null,
    val image_url: String? = null,
    val images: Map<String, String>? = null,
    val video_url: String? = null,
    val is_video: Boolean? = null,
    val uploader: PinterestUploader? = null,
)

@Serializable
public data class PinterestApiResult(
    val query: String? = null,
    val count: Int? = null,
    val result: List<PinterestPin>? = null,
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
    val image: String? = null,
    val images: Map<String, JsonObject>? = null,
    val is_video: Boolean? = null,
    val video_url: String? = null,
    val user: PinterestUploader? = null,
)

@Serializable
public data class PinterestResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: PinterestApiResult? = null,
)

// ── All-In-One (AIO) ──────────────────────────────────────────────────────

@Serializable
public data class AioResult(
    val status: String? = null,
    val mess: String? = null,
    val p: String? = null,
    val vid: String? = null,
    val title: String? = null,
    val t: Int? = null,
    val a: String? = null,
    val links: JsonObject? = null,
    val related: List<JsonObject>? = null,
)

@Serializable
public data class AioResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: AioResult? = null,
    public val data: JsonObject? = null,
    public val mp4: JsonObject? = null,
    public val mp3: JsonObject? = null,
)

// ── Xiaohongshu ───────────────────────────────────────────────────────────

@Serializable
public data class XHSAuthor(
    val id: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val profileUrl: String? = null,
)

@Serializable
public data class XHSEngagement(
    val likes: Int? = null,
    val comments: Int? = null,
    val collects: Int? = null,
    val shares: Int? = null,
)

@Serializable
public data class XHSDownloadLink(
    val quality: String? = null,
    val url: String? = null,
)

@Serializable
public data class XHSNoteResult(
    val noteId: String? = null,
    val author: XHSAuthor? = null,
    val title: String? = null,
    val desc: String? = null,
    val keywords: String? = null,
    val duration: String? = null,
    val engagement: XHSEngagement? = null,
    val images: List<String>? = null,
    val downloads: List<XHSDownloadLink>? = null,
)

@Serializable
public data class XiaohongshuResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: XHSNoteResult? = null,
)

// ── Xiaohongshu Profile ───────────────────────────────────────────────────

@Serializable
public data class XHSProfileUser(
    val id: String? = null,
    val redId: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val profileUrl: String? = null,
    val bio: String? = null,
    val gender: Int? = null,
    val ipLocation: String? = null,
    val verified: Boolean? = null,
    val verifyType: Int? = null,
)

@Serializable
public data class XHSProfileStats(
    val followers: Int? = null,
    val followings: Int? = null,
    val likes: Int? = null,
    val notes: Int? = null,
)

@Serializable
public data class XHSProfileNote(
    val noteId: String? = null,
    val title: String? = null,
    val type: String? = null,
    val cover: String? = null,
    val likes: Int? = null,
)

@Serializable
public data class XHSPagination(
    val hasMore: Boolean? = null,
    val nextCursor: String? = null,
)

@Serializable
public data class XHSProfileResult(
    val status: Boolean? = null,
    val user: XHSProfileUser? = null,
    val stats: XHSProfileStats? = null,
    val notes: List<XHSProfileNote>? = null,
    val pagination: XHSPagination? = null,
)

@Serializable
public data class XiaohongshuProfileResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: XHSProfileResult? = null,
)

// ── Douyin ────────────────────────────────────────────────────────────────

@Serializable
public data class DouyinLink(
    val quality: String? = null,
    val url: String? = null,
)

@Serializable
public data class DouyinResult(
    val status: Boolean? = null,
    val title: String? = null,
    val thumbnail: String? = null,
    val links: List<DouyinLink>? = null,
)

@Serializable
public data class DouyinResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: DouyinResult? = null,
)

// ── SnackVideo ────────────────────────────────────────────────────────────

@Serializable
public data class SnackVideoInteraction(
    val views: Int? = null,
    val likes: Int? = null,
    val shares: Int? = null,
)

@Serializable
public data class SnackVideoCreator(
    val name: String? = null,
    val profileUrl: String? = null,
    val bio: String? = null,
)

@Serializable
public data class SnackVideoResult(
    val status: Boolean? = null,
    val url: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumbnail: String? = null,
    val uploadDate: String? = null,
    val videoUrl: String? = null,
    val duration: String? = null,
    val interaction: SnackVideoInteraction? = null,
    val creator: SnackVideoCreator? = null,
)

@Serializable
public data class SnackVideoResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: SnackVideoResult? = null,
)

// ── Cocofun ───────────────────────────────────────────────────────────────

@Serializable
public data class CocofunResult(
    val status: Boolean? = null,
    val topic: String? = null,
    val caption: String? = null,
    val play: Int? = null,
    val like: Int? = null,
    val share: Int? = null,
    val duration: Int? = null,
    val thumbnail: String? = null,
    val watermark: String? = null,
    val no_watermark: String? = null,
)

@Serializable
public data class CocofunResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: CocofunResult? = null,
)

// ── Spotify ───────────────────────────────────────────────────────────────

@Serializable
public data class SpotifyFormat(
    val status: Boolean? = null,
    val url: String? = null,
    val filesize: String? = null,
    val quality: String? = null,
    val acodec: String? = null,
    val vcodec: String? = null,
    val ext: String? = null,
    val protocol: String? = null,
)

@Serializable
public data class SpotifyResult(
    val status: Boolean? = null,
    val title: String? = null,
    val source: String? = null,
    val server: String? = null,
    val thumbnail: String? = null,
    val duration: Double? = null,
    val message: String? = null,
    val subtitles: List<String>? = null,
    val formats: List<SpotifyFormat>? = null,
)

@Serializable
public data class SpotifyResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: SpotifyResult? = null,
)

// ── YouTube Search (YTS) ──────────────────────────────────────────────────

@Serializable
public data class YtsVideo(
    val status: Boolean? = null,
    val title: String? = null,
    val url: String? = null,
    val videoId: String? = null,
    val author: String? = null,
    val authorId: String? = null,
    val authorUrl: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val views: Int? = null,
    val uploadedAt: String? = null,
    val thumbnail: String? = null,
    val type: String? = null,
)

@Serializable
public data class YtsResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: YtsVideo? = null,
)

// ── SoundCloud ────────────────────────────────────────────────────────────

@Serializable
public data class SoundCloudResult(
    val status: Boolean? = null,
    val title: String? = null,
    val thumbnail: String? = null,
    val audio: String? = null,
    val downloadMp3: String? = null,
    val downloadArtwork: String? = null,
)

@Serializable
public data class SoundCloudResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: SoundCloudResult? = null,
)

// ── Threads ───────────────────────────────────────────────────────────────

@Serializable
public data class ThreadsResult(
    val status: Boolean? = null,
    val type: String? = null,
    val image: String? = null,
    val video: String? = null,
)

@Serializable
public data class ThreadsResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: ThreadsResult? = null,
)

// ── Kuaishou ──────────────────────────────────────────────────────────────

@Serializable
public data class KuaishouResult(
    val success: Boolean? = null,
    val videoUrl: String? = null,
    val title: String? = null,
    val author: String? = null,
    val username: String? = null,
    val likeCount: Int? = null,
    val commentCount: Int? = null,
    val viewCount: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,
    val rawUrl: String? = null,
)

@Serializable
public data class KuaishouResponse(
    public val status: Boolean? = null,
    public val developer: String? = null,
    public val message: String? = null,
    public val note: String? = null,
    public val code: Int? = null,
    public val result: KuaishouResult? = null,
)
