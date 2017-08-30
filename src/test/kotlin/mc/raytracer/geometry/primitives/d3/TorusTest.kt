package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.Hit
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import org.junit.Test
import kotlin.test.assertTrue

class TorusTest {
    @Test
    fun hitWorks() {
        val tori = Torus(50.0, 10.0)
        val ray = Ray.create(Point3D.zero, -Vector3D.axisZ)

        val result = tori.hit(ray)

        assertTrue { result is Hit }

        val hit = (result as Hit)

    }
}