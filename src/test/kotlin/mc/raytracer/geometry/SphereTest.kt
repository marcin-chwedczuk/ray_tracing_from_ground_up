package mc.raytracer.geometry

import mc.raytracer.geometry.primitives.d3.Sphere
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import org.junit.Test
import kotlin.test.assertTrue

class SphereTest {

    @Test
    fun hitWorks() {
        val sphere = Sphere(Point3D(0, 0, 0), 10.0)
        val ray = Ray(Point3D(0,0,100), Vector3D(0,0,-1))

        val result = sphere.hit(ray)

        assertTrue(result is Hit)
    }
}
