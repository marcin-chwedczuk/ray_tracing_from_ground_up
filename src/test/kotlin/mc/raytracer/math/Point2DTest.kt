package mc.raytracer.math

import org.junit.jupiter.api.Test


class Point2DTest {
    @Test
    fun timesWorks() {
        val point = Point2D(1,2)

        val result = point * 3.0

        assertEquals(3.0, result.x, message="component x")
        assertEquals(6.0, result.y, message="component x")
    }

    @Test
    fun doubleTimesWorks() {
        val point = Point2D(1,2)

        val result = 3.0 * point

        assertEquals(3.0, result.x, message="component x")
        assertEquals(6.0, result.y, message="component x")

    }
}