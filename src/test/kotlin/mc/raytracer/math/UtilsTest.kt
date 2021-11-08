package mc.raytracer.math

import org.junit.jupiter.api.Test

class UtilsTest {
    @Test
    fun degToRadWorks() {
        assertEquals(0.0, 0.0.degToRad())
        assertEquals(Math.PI, 180.0.degToRad())
        assertEquals(2*Math.PI, 360.0.degToRad())
        assertEquals(0.5235987756, 30.0.degToRad())
    }

    @Test
    fun radToDegWorks() {
        assertEquals(0.0, 0.0.radToDeg())
        assertEquals(90.0, (Math.PI/2).radToDeg())
        assertEquals(360.0, (2*Math.PI).radToDeg())
        assertEquals(60.0, (Math.PI/3).radToDeg())
    }
}
