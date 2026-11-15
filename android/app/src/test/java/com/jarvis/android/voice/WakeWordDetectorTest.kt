package com.jarvis.android.voice

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WakeWordDetectorTest {

    private val detector = WakeWordDetector()

    @Test
    fun `detects persian wake word`() {
        assertTrue(detector.check("هی جارویس تلگرام رو باز کن"))
    }

    @Test
    fun `detects english wake word case-insensitively`() {
        assertTrue(detector.check("Hey Jarvis, what's the weather?"))
        assertTrue(detector.check("HEY JARVIS"))
    }

    @Test
    fun `does not trigger on unrelated speech`() {
        assertFalse(detector.check("سلام، امروز هوا چطور است؟"))
        assertFalse(detector.check("open the pod bay doors"))
    }

    @Test
    fun `false detection state can be reset`() {
        detector.check("هی جارویس")
        detector.reset()
        assertFalse(detector.detected.value)
    }
}
