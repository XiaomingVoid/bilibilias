package com.imcys.bilibilias.common.data.value

import org.junit.Assert.assertEquals
import org.junit.Test

class BiliImageURLTest {

    private val baseJpg =
        "https://i1.hdslb.com/bfs/archive/x.jpg"

    @Test
    fun `width should append param after at`() {
        val actual = BiliImageURL(baseJpg).width(100).toString()
        assertEquals("$baseJpg@100w", actual)
    }

    @Test
    fun `height should append param after at`() {
        val actual = BiliImageURL(baseJpg).height(100).toString()
        assertEquals("$baseJpg@100h", actual)
    }

    @Test
    fun `multi params should be joined by underscore`() {
        val actual = BiliImageURL(baseJpg)
            .width(10)
            .height(20)
            .quality(1)
            .toString()
        assertEquals("$baseJpg@10w_20h_1q", actual)
    }

    @Test
    fun `format should always use at and trailing dot format`() {
        val actual = BiliImageURL(baseJpg).webp().toString()
        assertEquals("$baseJpg@.webp", actual)
    }

    @Test
    fun `format with existing params should remain at end only once`() {
        val actual = BiliImageURL(baseJpg)
            .quality(1)
            .webp()
            .avif()
            .toString()
        assertEquals("$baseJpg@1q.avif", actual)
    }

    @Test
    fun `source should use exclamation and keep format at end`() {
        val actual = BiliImageURL(baseJpg)
            .width(10)
            .height(20)
            .source("web-home-carousel-cover")
            .avif()
            .toString()
        assertEquals("$baseJpg@10w_20h_!web-home-carousel-cover.avif", actual)
    }

    @Test
    fun `source without params should not add redundant underscore`() {
        val actual = BiliImageURL(baseJpg)
            .source("header")
            .toString()
        assertEquals("$baseJpg@!header", actual)
    }

    @Test
    fun `should preserve original extension before at`() {
        val actual = BiliImageURL(baseJpg).width(10).toString()
        assertEquals("$baseJpg@10w", actual)
    }

    @Test
    fun `toHttps should replace protocol only`() {
        val actual = BiliImageURL("http://i0.xxx.com/bfs/banner/xxx.png")
            .toHttps()
            .toString()
        assertEquals("https://i0.xxx.com/bfs/banner/xxx.png", actual)
    }

    @Test
    fun `should preserve query and fragment`() {
        val actual = BiliImageURL("$baseJpg?x=1#anchor")
            .width(100)
            .webp()
            .toString()
        assertEquals("$baseJpg@100w.webp?x=1#anchor", actual)
    }

    @Test
    fun `malformed duplicate format in params should be normalized`() {
        val actual = BiliImageURL("$baseJpg@.avif_10w_20h.webp")
            .avif()
            .toString()
        assertEquals("$baseJpg@10w_20h.avif", actual)
    }

    @Test
    fun `existing source format url should keep single trailing format`() {
        val actual = BiliImageURL("$baseJpg@10w_20h_!web-home-carousel-cover.avif")
            .webp()
            .toString()
        assertEquals("$baseJpg@10w_20h_!web-home-carousel-cover.webp", actual)
    }

    @Test
    fun `mixed operation chains should produce canonical urls`() {
        val cases = listOf(
            Triple("https no-op on https", BiliImageURL(baseJpg).toHttps().toString(), baseJpg),
            Triple("single size param", BiliImageURL(baseJpg).width(10).toHttps().toString(), "$baseJpg@10w"),
            Triple(
                "size and quality params",
                BiliImageURL(baseJpg).width(10).height(20).quality(100).toString(),
                "$baseJpg@10w_20h_100q"
            ),
            Triple("format only", BiliImageURL(baseJpg).avif().toString(), "$baseJpg@.avif"),
            Triple("format then params", BiliImageURL(baseJpg).avif().width(10).height(20).toString(), "$baseJpg@10w_20h.avif"),
            Triple(
                "format override keeps single trailing format",
                BiliImageURL(baseJpg).avif().width(10).height(20).webp().toString(),
                "$baseJpg@10w_20h.webp"
            ),
            Triple("source + format", BiliImageURL(baseJpg).source("baner").webp().toString(), "$baseJpg@!baner.webp"),
            Triple("avg color pseudo-format", BiliImageURL(baseJpg).avgColor().toString(), "$baseJpg@.avg_color")
        )

        cases.forEach { (name, actual, expected) ->
            assertEquals(name, expected, actual)
        }
    }

    @Test
    fun `format before or after size params should normalize to same output`() {
        val a = BiliImageURL(baseJpg).avif().width(10).height(20).toString()
        val b = BiliImageURL(baseJpg).width(10).height(20).avif().toString()
        assertEquals("$baseJpg@10w_20h.avif", a)
        assertEquals(a, b)
    }

    @Test
    fun `source before or after size params should normalize to same output`() {
        val a = BiliImageURL(baseJpg).source("header").width(10).height(20).toString()
        val b = BiliImageURL(baseJpg).width(10).height(20).source("header").toString()
        assertEquals("$baseJpg@10w_20h_!header", a)
        assertEquals(a, b)
    }
}
