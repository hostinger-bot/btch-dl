package io.btch.downloader

import io.btch.downloader.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

/**
 * Ktor-based API client for btch-downloader — download media from 20+ social platforms.
 *
 * Uses [Ktor HttpClient](https://ktor.io) under the hood.  Every method calls
 * `GET {baseUrl}/{endpoint}?url={input}` and returns a typed response.
 *
 * ## Quick start
 *
 * ```kotlin
 * val api = BtchDownloader()
 *
 * suspend fun main() {
 *     val tiktok = api.ttdl("https://www.tiktok.com/@user/video/123")
 *     println(tiktok.video)
 * }
 * ```
 *
 * @param config Client configuration (base URL, timeout, logging, etc.).
 *               Uses sensible defaults when omitted.
 */
public class BtchDownloader(
    private val config: BtchConfig = BtchConfig(),
) : AutoCloseable {

    private val client: HttpClient = config.buildClient()
    private val json: Json = config.json

    // ── low-level helper ──────────────────────────────────────────────────

    /**
     * Raw API call.  Public so callers can reach undocumented endpoints.
     *
     * @param endpoint  API path segment (e.g. `"ttdl"`)
     * @param input     URL or query string to pass as the `url` parameter
     * @return          Raw JSON [String] from the backend
     * @throws Exception on non-2xx response
     */
    public suspend fun raw(endpoint: String, input: String): String {
        val response = client.get(endpoint) {
            parameter("url", input)
        }
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: $body")
        }
        return body
    }

    /**
     * Typed API call used internally by every convenience method.
     */
    private suspend inline fun <reified T> fetch(endpoint: String, input: String): T {
        val response = client.get(endpoint) {
            parameter("url", input)
        }
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: $body")
        }
        return json.decodeFromString(body)
    }

    // ── Platform methods ───────────────────────────────────────────────────

    /** TikTok video/audio downloader. */
    public suspend fun ttdl(url: String): TikTokResponse = fetch("ttdl", url)

    /** Instagram Reels/Posts/TV/Stories downloader. */
    public suspend fun igdl(url: String): InstagramResponse = fetch("igdl", url)

    /** Twitter (X) video downloader. */
    public suspend fun twitter(url: String): TwitterResponse = fetch("twitter", url)

    /** YouTube video & audio downloader. */
    public suspend fun youtube(url: String): YouTubeResponse = fetch("youtube", url)

    /** Facebook video downloader. */
    public suspend fun fbdown(url: String): FacebookResponse = fetch("fbdown", url)

    /** MediaFire file downloader (⚠ unmaintained). */
    public suspend fun mediafire(url: String): MediaFireResponse = fetch("mediafire", url)

    /** CapCut template downloader. */
    public suspend fun capcut(url: String): CapCutResponse = fetch("capcut", url)

    /** All-In-One downloader (⚠ unmaintained) — supports any platform. */
    public suspend fun aio(url: String): AioResponse = fetch("aio", url)

    /** Google Drive file downloader. */
    public suspend fun gdrive(url: String): GoogleDriveResponse = fetch("gdrive", url)

    /**
     * Pinterest content downloader or search.
     * @param query A Pinterest URL **or** a search keyword.
     */
    public suspend fun pinterest(query: String): PinterestResponse = fetch("pinterest", query)

    /** Xiaohongshu (小红书) note downloader. */
    public suspend fun xiaohongshu(url: String): XiaohongshuResponse = fetch("xiaohongshu", url)

    /** Xiaohongshu (小红书) user profile metadata. */
    public suspend fun xiaohongshuProfile(url: String): XiaohongshuProfileResponse =
        fetch("xiaohongshu_profile", url)

    /** Douyin (抖音) video/image downloader. */
    public suspend fun douyin(url: String): DouyinResponse = fetch("douyin", url)

    /** SnackVideo content downloader. */
    public suspend fun snackvideo(url: String): SnackVideoResponse = fetch("snackvideo", url)

    /** Cocofun content downloader. */
    public suspend fun cocofun(url: String): CocofunResponse = fetch("cocofun", url)

    /** Spotify track downloader. */
    public suspend fun spotify(url: String): SpotifyResponse = fetch("spotify", url)

    /** YouTube search engine. */
    public suspend fun yts(query: String): YtsResponse = fetch("yts", query)

    /** SoundCloud track downloader. */
    public suspend fun soundcloud(url: String): SoundCloudResponse = fetch("soundcloud", url)

    /** Threads (by Instagram) content downloader. */
    public suspend fun threads(url: String): ThreadsResponse = fetch("threads", url)

    /** Kuaishou (快手) video downloader. */
    public suspend fun kuaishou(url: String): KuaishouResponse = fetch("kuaishou", url)

    // ── Lifecycle ──────────────────────────────────────────────────────────

    /** Release underlying HTTP resources.  Call when done, or use `use {}`. */
    override fun close() {
        client.close()
    }
}
