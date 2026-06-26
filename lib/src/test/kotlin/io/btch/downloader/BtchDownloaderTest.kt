package io.btch.downloader

import io.btch.downloader.model.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BtchDownloaderTest {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    /**
     * Creates a [BtchDownloader] whose HTTP calls return [body] for any request.
     */
    private fun mockClient(body: String): BtchDownloader {
        val engine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(body),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return BtchDownloader(
            BtchConfig(httpClient = HttpClient(engine))
        )
    }

    // ── TikTok ────────────────────────────────────────────────────────────

    @Test
    fun `ttdl returns TikTokResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"title":"My Video","title_audio":"Audio","thumbnail":"https://thumb.com","video":["https://vid.com"],"audio":["https://aud.com"]}
        """.trimIndent())

        val res = client.ttdl("https://tiktok.com/@u/v/123")
        assertEquals(true, res.status)
        assertEquals("My Video", res.title)
        assertEquals(1, res.video?.size)
    }

    // ── Instagram ─────────────────────────────────────────────────────────

    @Test
    fun `igdl returns InstagramResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":[{"thumbnail":"https://t.com","url":"https://u.com"}]}
        """.trimIndent())

        val res = client.igdl("https://instagram.com/reel/123")
        assertEquals(true, res.status)
        assertNotNull(res.result)
        assertEquals(1, res.result?.size)
    }

    // ── Twitter ───────────────────────────────────────────────────────────

    @Test
    fun `twitter returns TwitterResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"title":"Tweet","url":"https://video.com"}
        """.trimIndent())

        val res = client.twitter("https://twitter.com/u/status/123")
        assertEquals("Tweet", res.title)
        assertNotNull(res.url)
    }

    // ── YouTube ───────────────────────────────────────────────────────────

    @Test
    fun `youtube returns YouTubeResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"title":"YT","thumbnail":"https://t.com","author":"Channel","mp3":"https://mp3","mp4":"https://mp4"}
        """.trimIndent())

        val res = client.youtube("https://youtu.be/abc")
        assertEquals("YT", res.title)
        assertNotNull(res.mp4)
        assertNotNull(res.mp3)
    }

    // ── Facebook ──────────────────────────────────────────────────────────

    @Test
    fun `fbdown returns FacebookResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"Normal_video":"https://sd","HD":"https://hd"}
        """.trimIndent())

        val res = client.fbdown("https://facebook.com/v/123")
        assertNotNull(res.HD)
        assertNotNull(res.Normal_video)
    }

    // ── CapCut ────────────────────────────────────────────────────────────

    @Test
    fun `capcut returns CapCutResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"title":"Template","originalVideoUrl":"https://vid","coverUrl":"https://img","authorName":"Author"}
        """.trimIndent())

        val res = client.capcut("https://capcut.com/t/123")
        assertEquals("Template", res.title)
        assertEquals("Author", res.authorName)
    }

    // ── Google Drive ──────────────────────────────────────────────────────

    @Test
    fun `gdrive returns GoogleDriveResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"filename":"file.pdf","filesize":"1024","downloadUrl":"https://dl"}}
        """.trimIndent())

        val res = client.gdrive("https://drive.google.com/file/d/abc")
        assertNotNull(res.result)
        assertEquals("file.pdf", res.result?.filename)
    }

    // ── Pinterest ─────────────────────────────────────────────────────────

    @Test
    fun `pinterest returns PinterestResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"query":"wallpaper","count":2,"result":[{"id":"1","title":"Pin","image_url":"https://img"}]}}
        """.trimIndent())

        val res = client.pinterest("https://pin.it/abc")
        assertEquals(true, res.status)
        assertEquals("wallpaper", res.result?.query)
    }

    // ── Xiaohongshu ───────────────────────────────────────────────────────

    @Test
    fun `xiaohongshu returns XiaohongshuResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"noteId":"abc","title":"XHS Post","desc":"desc"}}
        """.trimIndent())

        val res = client.xiaohongshu("http://xhslink.com/abc")
        assertEquals("XHS Post", res.result?.title)
    }

    // ── Xiaohongshu Profile ───────────────────────────────────────────────

    @Test
    fun `xiaohongshuProfile returns profile`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"user":{"nickname":"User123","followerCount":100}}}
        """.trimIndent())

        val res = client.xiaohongshuProfile("https://www.xiaohongshu.com/user/profile/abc")
        assertEquals("User123", res.result?.user?.nickname)
    }

    // ── Douyin ────────────────────────────────────────────────────────────

    @Test
    fun `douyin returns DouyinResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"title":"Douyin Vid","thumbnail":"https://t.com","links":[{"quality":"hd","url":"https://v.com"}]}}
        """.trimIndent())

        val res = client.douyin("https://v.douyin.com/abc")
        assertEquals("Douyin Vid", res.result?.title)
        assertEquals(1, res.result?.links?.size)
    }

    // ── SnackVideo ────────────────────────────────────────────────────────

    @Test
    fun `snackvideo returns SnackVideoResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"title":"SV","videoUrl":"https://v.com","creator":{"name":"Creator"}}}
        """.trimIndent())

        val res = client.snackvideo("https://s.snackvideo.com/p/abc")
        assertEquals("SV", res.result?.title)
        assertEquals("Creator", res.result?.creator?.name)
    }

    // ── Cocofun ───────────────────────────────────────────────────────────

    @Test
    fun `cocofun returns CocofunResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"topic":"funny","caption":"lol","watermark":"https://wm","no_watermark":"https://nowm"}}
        """.trimIndent())

        val res = client.cocofun("https://www.icocofun.com/share/post/123")
        assertEquals("funny", res.result?.topic)
        assertNotNull(res.result?.no_watermark)
    }

    // ── Spotify ───────────────────────────────────────────────────────────

    @Test
    fun `spotify returns SpotifyResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"title":"Track","thumbnail":"https://t.com","formats":[{"url":"https://mp3","quality":"320kbps","ext":"mp3"}]}}
        """.trimIndent())

        val res = client.spotify("https://open.spotify.com/track/abc")
        assertEquals("Track", res.result?.title)
        assertNotNull(res.result?.formats)
    }

    // ── YouTube Search ────────────────────────────────────────────────────

    @Test
    fun `yts returns YtsResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"title":"Movie Trailer","url":"https://youtube.com/watch?v=abc","views":1000}}
        """.trimIndent())

        val res = client.yts("movie 2024")
        assertEquals("Movie Trailer", res.result?.title)
    }

    // ── SoundCloud ────────────────────────────────────────────────────────

    @Test
    fun `soundcloud returns SoundCloudResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"title":"SC Track","audio":"https://au.com","downloadMp3":"https://mp3.com"}}
        """.trimIndent())

        val res = client.soundcloud("https://soundcloud.com/u/track")
        assertEquals("SC Track", res.result?.title)
        assertNotNull(res.result?.audio)
    }

    // ── Threads ───────────────────────────────────────────────────────────

    @Test
    fun `threads returns ThreadsResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"type":"video","image":"https://img","video":"https://vid"}}
        """.trimIndent())

        val res = client.threads("https://www.threads.net/@u/post/abc")
        assertEquals("video", res.result?.type)
        assertNotNull(res.result?.video)
    }

    // ── Kuaishou ──────────────────────────────────────────────────────────

    @Test
    fun `kuaishou returns KuaishouResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"success":true,"videoUrl":"https://v.com","title":"KS Video","author":"Author","likeCount":100}}
        """.trimIndent())

        val res = client.kuaishou("https://v.kuaishou.com/abc")
        assertEquals(true, res.result?.success)
        assertEquals("KS Video", res.result?.title)
    }

    // ── All-In-One ────────────────────────────────────────────────────────

    @Test
    fun `aio returns AioResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"status":"ok","title":"AIO Title","vid":"https://vid.com"}}
        """.trimIndent())

        val res = client.aio("https://tiktok.com/@u/v/123")
        assertEquals("ok", res.result?.status)
        assertEquals("AIO Title", res.result?.title)
    }

    // ── MediaFire ─────────────────────────────────────────────────────────

    @Test
    fun `mediafire returns MediaFireResponse`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":true,"result":{"filename":"app.zip","filesize":"15MB","url":"https://dl.com"}}
        """.trimIndent())

        val res = client.mediafire("https://www.mediafire.com/file/abc")
        assertEquals("app.zip", res.result?.filename)
        assertNotNull(res.result?.url)
    }

    // ── Error response ────────────────────────────────────────────────────

    @Test
    fun `non-ok status still parses`() = runTest {
        val client = mockClient("""
            {"developer":"test","status":false,"message":"Invalid URL","note":"Check the URL"}
        """.trimIndent())

        val res = client.ttdl("bad-url")
        assertEquals(false, res.status)
        assertEquals("Invalid URL", res.message)
    }

    // ── Raw helper ───────────────────────────────────────────────────────

    @Test
    fun `raw returns unparsed JSON`() = runTest {
        val client = mockClient("""{"raw":true}""".trimIndent())
        val raw = client.raw("ping", "test")
        assertTrue(raw.contains("raw"))
    }
}
