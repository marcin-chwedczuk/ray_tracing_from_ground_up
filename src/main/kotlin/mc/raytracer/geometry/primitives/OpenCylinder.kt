package mc.raytracer.geometry.primitives

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Ray
import java.lang.Math.sqrt

class OpenCylinder(
        var yBottom: Double,
        var yTop: Double,
        var radius: Double
): GeometricObject() {

    override fun hit(ray: Ray): HitResult {
        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z

        val a = dx * dx + dz * dz
        val b = 2.0 * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val delta = b * b - 4.0 * a * c

        if (delta < 0.0)
            return Miss.instance

        val deltaSqrt = sqrt(delta)
        val _2a = 2.0 * a

        val t1 = (-b - deltaSqrt) / _2a    // smaller root
        if (t1 > K_EPSILON) {
            val yHit = oy + t1 * dy

            if (yHit > yBottom && yHit < yTop) {
                var normal = Normal3D((ox + t1 * dx) / radius, 0.0, (oz + t1 * dz) / radius)

                if ((-ray.direction dot normal) < 0.0)
                    normal = -normal

                return Hit(
                    tmin = t1,
                    localHitPoint = ray.origin + ray.direction*t1,
                    normalAtHitPoint = normal)
            }
        }

        val t2 = (-b + deltaSqrt) / _2a
        if (t2 > K_EPSILON) {
            val yHit = oy + t2 * dy

            if (yHit > yBottom && yHit < yTop) {
                var normal = Normal3D((ox + t2 * dx) / radius, 0.0, (oz + t2 * dz) / radius)

                if ((-ray.direction dot normal) < 0.0)
                    normal = -normal

                return Hit(
                    tmin = t2,
                    localHitPoint = ray.origin + ray.direction*t2,
                    normalAtHitPoint = normal)
            }
        }

        return Miss.instance
    }

}
