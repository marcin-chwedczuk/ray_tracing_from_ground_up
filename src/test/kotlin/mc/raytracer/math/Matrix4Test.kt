package mc.raytracer.math

import org.junit.Test
import kotlin.test.assertEquals

class Matrix4Test {
    @Test
    fun identityWorks() {
        val m = Matrix4.identity

        for (row in 0..3) {
            for (col in 0..3) {
                if (row == col) {
                    assertEquals(1.0, m[row,col],
                        message="failed for row: $row and col: $col")
                }
                else {
                    assertEquals(0.0, m[row,col],
                        message="failed for row: $row and col: $col")
                }
            }
        }
    }

    @Test
    fun zeroWorks() {
        val m = Matrix4.zero

         for (row in 0..3) {
            for (col in 0..3) {
                assertEquals(0.0, m[row,col],
                    message="failed for row: $row and col: $col")
            }
        }
    }

    @Test
    fun toStringWorks() {
        val m = Matrix4(arrayOf(
            1.0,  2.0,  3.0,  4.0,
            5.0,  6.0,  7.0,  8.0,
            9.0,  10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        ))

        val expected =
"""[1.000, 2.000, 3.000, 4.000
5.000, 6.000, 7.000, 8.000,
9.000, 10.000, 11.000, 12.000,
13.000, 14.000, 15.000, 16.000]"""

        assertEquals(expected, m.toString())
    }

    @Test
    fun divWorks() {
        val m = Matrix4(arrayOf(
            4.0, 2.0, 2.0, 2.0,
            2.0, 4.0, 2.0, 2.0,
            2.0, 2.0, 4.0, 2.0,
            2.0, 2.0, 2.0, 4.0
        ))

        val result = m / 2.0

        val expected = Matrix4(arrayOf(
            2.0, 1.0, 1.0, 1.0,
            1.0, 2.0, 1.0, 1.0,
            1.0, 1.0, 2.0, 1.0,
            1.0, 1.0, 1.0, 2.0
        ))

        for (row in 0..3) {
            for (col in 0..3) {
                assertEquals(expected[row,col], result[row,col])
            }
        }
    }

    @Test
    fun timesWorks() {
        val a = Matrix4(arrayOf(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        ))

        val b = Matrix4(arrayOf(
            2.0, 4.0, 3.0, 7.0,
            1.0, 5.0, 5.0, 6.0,
            7.0, 3.0, 9.0, 3.0,
            1.0, 4.0, 9.0, 2.0
        ))

        val result = a*b

        val expected = Matrix4(arrayOf(
            29.0, 39.0, 76.0, 36.0,
            73.0, 103.0, 180.0, 108.0,
            117.0, 167.0, 284.0, 180.0,
            161.0, 231.0, 388.0, 252.0
        ))

        for (row in 0..3) {
            for (col in 0..3) {
                assertEquals(
                    expected[row,col],
                    result[row,col],
                    "failure for row: $row, col: $col")
            }
        }
    }

    @Test
    fun timesWithVectorWorks() {
        val m = Matrix4(arrayOf(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        ))

        val vec = Vector3D(1,2,3)

        val result = m*vec

        assertEquals(14.0, result.x, message = "component x")
        assertEquals(38.0, result.y, message = "component y")
        assertEquals(62.0, result.z, message = "component z")
    }

    @Test
    fun timesWithNormalWorks() {
        val m = Matrix4(arrayOf(
                1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 8.0,
                9.0, 10.0, 11.0, 12.0,
                13.0, 14.0, 15.0, 16.0
        ))

        val normal = Normal3D(1,2,3)

        val result = m*normal

        assertEquals(14.0, result.x, message = "component x")
        assertEquals(38.0, result.y, message = "component y")
        assertEquals(62.0, result.z, message = "component z")
    }
}