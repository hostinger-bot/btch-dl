package io.btch.downloader

import kotlinx.coroutines.runBlocking
import org.junit.Test

class RawOutputTest {
    @Test
    fun testRaw() = runBlocking {
        val api = BtchDownloader()
        val urls = mapOf(
            "douyin" to "https://v.douyin.com/idk",
            "spotify" to "https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT",
            "xiaohongshu" to "https://www.xiaohongshu.com/explore/123",
            "pinterest" to "https://pin.it/123"
        )
        for ((p, u) in urls) {
            try {
                println("== $p ==")
                println(api.raw(p, u))
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
