package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox

public class Box(
    val minCorner: Point3D,
    val maxCorner: Point3D
) : GeometricObject() {

    init {
        if (minCorner.x > maxCorner.x) throw IllegalArgumentException("minCorner.x must be less than maxCorner.x")
        if (minCorner.y > maxCorner.y) throw IllegalArgumentException("minCorner.y must be less than maxCorner.y")
        if (minCorner.z > maxCorner.z) throw IllegalArgumentException("minCorner.z must be less than maxCorner.z")
    }

    private val _boundingBox = BoundingBox.fromMinMaxPoints(minCorner, maxCorner)
    override val boundingBox: BoundingBox
        get() = _boundingBox

    public override fun hit(ray: Ray): HitResult {
        val ox = ray.origin.x;    val oy = ray.origin.y; 	val oz = ray.origin.z
        val dx = ray.direction.x; val dy = ray.direction.y; val dz = ray.direction.z

        var tx_min = 0.0; var ty_min = 0.0; var tz_min = 0.0
        var tx_max = 0.0; var ty_max = 0.0; var tz_max = 0.0

        val a = 1.0 / dx
        if (a >= 0) {
            tx_min = (minCorner.x - ox) * a
            tx_max = (maxCorner.x - ox) * a
        }
        else {
            tx_min = (maxCorner.x - ox) * a
            tx_max = (minCorner.x - ox) * a
        }

        val b = 1.0 / dy
        if (b >= 0) {
            ty_min = (minCorner.y - oy) * b
            ty_max = (maxCorner.y - oy) * b
        }
        else {
            ty_min = (maxCorner.y - oy) * b
            ty_max = (minCorner.y - oy) * b
        }

        val c = 1.0 / dz
        if (c >= 0) {
            tz_min = (minCorner.z - oz) * c
            tz_max = (maxCorner.z - oz) * c
        }
        else {
            tz_min = (maxCorner.z - oz) * c
            tz_max = (minCorner.z - oz) * c
        }

        // find largest entering t value
        var t0: Double; var t1: Double
        var face_in: Int; var face_out: Int

        if (tx_min > ty_min) {
            t0 = tx_min
            face_in = if (a >= 0.0) 0 else 3
        } else {
            t0 = ty_min
            face_in = if (b >= 0.0) 1 else 4
        }

        if (tz_min > t0) {
            t0 = tz_min
            face_in = if (c >= 0.0) 2 else 5
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max
            face_out = if (a >= 0.0) 3 else 0
        } else {
            t1 = ty_max
            face_out = if (b >= 0.0) 4 else 1
        }

        if (tz_max < t1) {
            t1 = tz_max
            face_out = if (c >= 0.0) 5 else 2
        }


        if (t0 < t1 && t1 > K_EPSILON) {
            val tmin = if (t0 > K_EPSILON) t0 else t1
            val face = if (t0 > K_EPSILON) face_in else face_out

            return Hit(
                    tmin = tmin,
                    localHitPoint = ray.pointOnRayPath(tmin),
                    normalAtHitPoint = getNormal(face),
                    material = material)
        }

        return Miss.instance
    }

    private fun getNormal(face: Int): Normal3D {
        when (face) {
            0 -> return Normal3D(-1, 0, 0) // -x
            1 -> return Normal3D(0, -1, 0) // -y
            2 -> return Normal3D(0, 0, -1) // -z
            3 -> return Normal3D(1, 0, 0)  // +x
            4 -> return Normal3D(0, 1, 0)  // +y
            5 -> return Normal3D(0, 0, 1)  // +z
        }

        throw IllegalArgumentException("Invalid box face: " + face)
    }

    public override fun shadowHit(ray: Ray): Double? {
        val ox = ray.origin.x;    val oy = ray.origin.y; 	val oz = ray.origin.z
        val dx = ray.direction.x; val dy = ray.direction.y; val dz = ray.direction.z

        var tx_min = 0.0; var ty_min = 0.0; var tz_min = 0.0
        var tx_max = 0.0; var ty_max = 0.0; var tz_max = 0.0

        val a = 1.0 / dx
        if (a >= 0) {
            tx_min = (minCorner.x - ox) * a
            tx_max = (maxCorner.x - ox) * a
        }
        else {
            tx_min = (maxCorner.x - ox) * a
            tx_max = (minCorner.x - ox) * a
        }

        val b = 1.0 / dy
        if (b >= 0) {
            ty_min = (minCorner.y - oy) * b
            ty_max = (maxCorner.y - oy) * b
        }
        else {
            ty_min = (maxCorner.y - oy) * b
            ty_max = (minCorner.y - oy) * b
        }

        val c = 1.0 / dz
        if (c >= 0) {
            tz_min = (minCorner.z - oz) * c
            tz_max = (maxCorner.z - oz) * c
        }
        else {
            tz_min = (maxCorner.z - oz) * c
            tz_max = (minCorner.z - oz) * c
        }

        // find largest entering t value
        val t0 = Math.max(tx_min, Math.max(ty_min, tz_min))

        // find smallest exiting t value
        val t1 = Math.min(tx_max, Math.min(ty_max, tz_max))

        if (t0 < t1 && t1 > K_EPSILON) {
            // hit
            return if (t0 > K_EPSILON) t0 else t1
        }

        // miss
        return null
    }

}
