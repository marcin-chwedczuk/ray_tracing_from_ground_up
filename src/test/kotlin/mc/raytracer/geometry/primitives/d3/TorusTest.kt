package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.Hit
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.*

class TorusTest {
    @Test
    fun hitWorks() {
        val tori = Torus(50.0, 10.0)
        val ray = Ray.create(Point3D.zero, -Vector3D.axisZ)

        val result = tori.hit(ray)

        assertThat(result)
            .isInstanceOf(Hit::class)

        // val hit = (result as Hit)
    }
}