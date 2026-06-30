package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NmeaParserTest {
    @Test
    fun parsesGgaAndRmcFromFixture() {
        val text = javaClass.classLoader
            ?.getResourceAsStream("gps/fixtures/nmea_sample.txt")
            ?.bufferedReader()
            ?.readText()
            ?: error("fixture missing")
        val fix = NmeaParser.parseBuffer(text)
        assertTrue(fix.valid)
        assertNotNull(fix.latitude)
        assertNotNull(fix.longitude)
        assertEquals(8, fix.numSatellites)
        assertEquals(0.9f, fix.hdop!!, 0.01f)
    }

    @Test
    fun parsesRmcSpeedAndCourse() {
        val line = "\$GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A"
        val fix = NmeaParser.parseLine(line)
        assertNotNull(fix)
        assertEquals(84.4f, fix!!.courseDeg!!, 0.1f)
    }

    @Test
    fun parsesGsaHdop() {
        val line = "\$GPGSA,A,3,04,05,09,12,,,,,,,,,2.5,1.3,2.1*39"
        val fix = NmeaParser.parseLine(line)
        assertNotNull(fix)
        assertEquals(1.3f, fix!!.hdop!!, 0.01f)
    }

    @Test
    fun parsesVtgSpeedKmh() {
        val line = "\$GPVTG,084.4,T,,M,022.4,N,041.4,K*48"
        val fix = NmeaParser.parseLine(line)
        assertNotNull(fix)
        assertEquals(84.4f, fix!!.courseDeg!!, 0.1f)
        assertEquals(41.4f / 3.6f, fix.speedMps!!, 0.1f)
    }
}
