package mc.raytracer.geometry

import mc.raytracer.geometry.primitives.d3.Sphere
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.*

class SphereTest {
    @Test
    fun hitWorks() {
        val sphere = Sphere(Point3D(0, 0, 0), 10.0)
        val ray = Ray.create(Point3D(0,0,100), Vector3D(0,0,-1))

        val result = sphere.hit(ray)

        assertThat(result)
            .isInstanceOf(Hit::class)
    }
}
