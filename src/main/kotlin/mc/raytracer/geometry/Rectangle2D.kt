package mc.raytracer.geometry

import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.*
import mc.raytracer.util.BoundingBox
import java.lang.Math.max
import java.lang.Math.min
import mc.raytracer.math.Vector3D
import mc.raytracer.util.ShadingInfo

class Rectangle2D(
        val point: Point3D,
        val spanA: Vector3D,
        val spanB: Vector3D)
    : GeometricObject() {

    val normal = (spanA cross spanB).norm()

    fun getBoundingBox(): BoundingBox {
	    val delta = 0.0001

        val a = spanA; val b = spanB; val p0 = point

	    return BoundingBox(
            min(p0.x, p0.x + a.x + b.x) - delta, max(p0.x, p0.x + a.x + b.x) + delta,
            min(p0.y, p0.y + a.y + b.y) - delta, max(p0.y, p0.y + a.y + b.y) + delta,
            min(p0.z, p0.z + a.z + b.z) - delta, max(p0.z, p0.z + a.z + b.z) + delta)
    }

    override fun hit(ray: Ray): HitResult {
        val p0 = point; val a = spanA; val b = spanB
        val t = ((p0 - ray.origin) dot normal) / (ray.direction dot normal)

        if (t <= K_EPSILON)
            return Miss.instance

        val p = ray.origin + t*ray.direction
        val d = p - p0

        val ddota = d dot a
        if (ddota < 0.0 || ddota > a.lengthSquared)
            return Miss.instance

        val ddotb = d dot b
        if (ddotb < 0.0 || ddotb > b.lengthSquared)
            return Miss.instance

        return Hit(tmin = t,
                localHitPoint = p,
                normalAtHitPoint = Normal3D.fromVector(normal))
    }
}
