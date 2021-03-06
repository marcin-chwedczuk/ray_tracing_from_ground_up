package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox
import java.lang.Math.sqrt

class OpenCylinder(
        var yBottom: Double,
        var yTop: Double,
        var radius: Double
): GeometricObject() {

    private val _boundingBox = BoundingBox.fromMinMaxPoints(
            Point3D(-radius, yBottom, -radius),
            Point3D(radius, yTop, radius))

    override val boundingBox: BoundingBox
        get() = _boundingBox

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
                    normalAtHitPoint = normal,
                    material = material)
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
                    normalAtHitPoint = normal,
                    material = material)
            }
        }

        return Miss.instance
    }

    override fun shadowHit(ray: Ray): Double? {
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
            return null

        val deltaSqrt = sqrt(delta)
        val _2a = 2.0 * a

        val t1 = (-b - deltaSqrt) / _2a    // smaller root
        if (t1 >= K_EPSILON) {
            val yHit = oy + t1 * dy

            if (yHit > yBottom && yHit < yTop) {
                return t1
            }
        }

        val t2 = (-b + deltaSqrt) / _2a
        if (t2 >= K_EPSILON) {
            val yHit = oy + t2 * dy

            if (yHit > yBottom && yHit < yTop) {
                return t2
            }
        }

        return null
    }
}
