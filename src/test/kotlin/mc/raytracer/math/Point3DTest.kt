package mc.raytracer.math


import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.*

class Point3DTest {

    @Test
    fun distanceToWorks() {
        val p1 = Point3D(1,2,3)
        val p2 = Point3D(7,7,7)

        val distance = p1.distanceTo(p2)

        assertEquals(8.77496438, distance)
    }

    @Test
    fun distanceToSquaredWorks() {
        val p1 = Point3D(1,2,3)
        val p2 = Point3D(7,7,7)

        val distance = p1.distanceToSquared(p2)

        assertEquals(77.0, distance)

    }

    @Test
    fun equalsWorks() {
        val p1 = Point3D(1,2,3)
        val p2 = Point3D(1,2,3)
        val p3 = Point3D(1.1,2.0,3.0)

        assertThat(p1)
            .isEqualTo(p2)

        assertThat(p1)
            .isNotEqualTo(p3)
    }

    @Test
    fun plusWorks() {
        val p = Point3D(1,2,3)
        val vec = Vector3D(11,12,13)

        val result: Point3D = p + vec

        assertEquals(12.0, result.x, message="x component")
        assertEquals(14.0, result.y, message="y component")
        assertEquals(16.0, result.z, message="z component")
    }

    @Test
    fun minusVectorWorks() {
        val p = Point3D(11,12,13)
        val vec = Vector3D(1,2,3)

        val result: Point3D = p - vec

        assertEquals(10.0, result.x, message="x component")
        assertEquals(10.0, result.y, message="y component")
        assertEquals(10.0, result.z, message="z component")
    }

    @Test
    fun minusPointWorks() {
        val p1 = Point3D(11,12,13)
        val p2 = Point3D(10,10,10)

        val result: Vector3D = p1 - p2

        assertEquals(1.0, result.x, message="x component")
        assertEquals(2.0, result.y, message="y component")
        assertEquals(3.0, result.z, message="z component")
    }

    @Test
    fun timesWorks() {
        val p = Point3D(1,2,3)

        val result: Point3D = p * 3.0

        assertEquals(3.0, result.x, message="x component")
        assertEquals(6.0, result.y, message="y component")
        assertEquals(9.0, result.z, message="z component")
     }

    @Test
    fun doubleTimesWorks() {
        val p = Point3D(1,2,3)

        val result: Point3D = 3.0 * p

        assertEquals(3.0, result.x, message="x component")
        assertEquals(6.0, result.y, message="y component")
        assertEquals(9.0, result.z, message="z component")
     }
}
