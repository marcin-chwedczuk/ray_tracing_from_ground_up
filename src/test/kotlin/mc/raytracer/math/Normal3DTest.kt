package mc.raytracer.math

import org.junit.Test

class Normal3DTest {
    @Test
    fun lengthWorks() {
        val vec = Normal3D(1.0, 2.0, 3.0)
        assertEquals(3.74165738677, vec.length)
    }

    @Test
    fun dotWithVectorWorks() {
        val a = Normal3D(1, 2, 3)
        val b = Vector3D(4, -5, 6)

        assertEquals(12.0, a.dot(b))
    }

    @Test
    fun normWorks() {
        val a = Normal3D(3, 1, 2)

        val norm = a.norm()

        assertEquals(0.80178372, norm.x, message = "component x")
        assertEquals(0.26726124, norm.y, message = "component y")
        assertEquals(0.53452248, norm.z, message = "component z")
    }

    @Test
    fun unaryMinusWorks() {
        val a = Normal3D(1, 2, 3)
        val b = -a

        assertEquals(-1.0, b.x, message = "component x")
        assertEquals(-2.0, b.y, message = "component y")
        assertEquals(-3.0, b.z, message = "component z")
    }

    @Test
    fun plusWorks() {
        val a = Normal3D(1, 2, 3)
        val b = Normal3D(10, 10, 10)

        val result = a + b

        assertEquals(11.0, result.x, message = "component x")
        assertEquals(12.0, result.y, message = "component y")
        assertEquals(13.0, result.z, message = "component z")
    }

    @Test
    fun plusWithVectorWorks() {
        val a = Normal3D(1, 2, 3)
        val b = Vector3D(10, 10, 10)

        val result = a + b

        assertEquals(11.0, result.x, message = "component x")
        assertEquals(12.0, result.y, message = "component y")
        assertEquals(13.0, result.z, message = "component z")
    }

    @Test
    fun timesWorks() {
        val a = Normal3D(1, 2, 3)

        val result = a * 2.0

        assertEquals(2.0, result.x, message = "component x")
        assertEquals(4.0, result.y, message = "component y")
        assertEquals(6.0, result.z, message = "component z")
    }

    @Test
    fun doubleTimesWorks() {
        val a = Normal3D(1, 2, 3)

        val result = 3.0 * a

        assertEquals(3.0, result.x, message = "component x")
        assertEquals(6.0, result.y, message = "component y")
        assertEquals(9.0, result.z, message = "component z")
    }
}