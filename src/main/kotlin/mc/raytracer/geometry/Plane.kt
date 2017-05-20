package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class Plane(
        val a: Point3D,
        val n: Normal3D)
    : GeometricObject() {

    /* Plane equation: p belongs to Plane
     * when (p-a) dot n = 0.
     */

    override fun hit(ray: Ray): HitResult {
        val t = (a-ray.origin).dot(n) / ray.direction.dot(n)

        if (t >= K_EPSILON) {
            return Hit(t, ShadingInfo(
                objectHit=true,
                localHitPoint=ray.origin + ray.direction*t,
                material=material,
                normalAtHitPoint=n))
        }

        return Miss.instance
    }
}
