package mc.raytracer.geometry

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Ray

class Box(
        val xMin: Double, val xMax: Double,
        val yMin: Double, val yMax: Double,
        val zMin: Double, val zMax: Double
) {
    init {
        if (xMin > xMax) throw IllegalArgumentException("xMin cannot be greater than xMax")
        if (yMin > yMax) throw IllegalArgumentException("yMin cannot be greater than yMax")
        if (zMin > zMax) throw IllegalArgumentException("zMin cannot be greater than zMax")
    }

    fun hit(ray: Ray): Boolean {
        // TODO: Check this code

        val ox = ray.origin.x;    val oy = ray.origin.y; 	val oz = ray.origin.z
	    val dx = ray.direction.x; val dy = ray.direction.y; val dz = ray.direction.z

        var tx_min = 0.0; var ty_min = 0.0; var tz_min = 0.0
        var tx_max = 0.0; var ty_max = 0.0; var tz_max = 0.0

        val a = 1.0 / dx
        if (a >= 0) {
            tx_min = (xMin - ox) * a
            tx_max = (xMax - ox) * a
        }
        else {
            tx_min = (xMax - ox) * a
            tx_max = (xMin - ox) * a
        }

        val b = 1.0 / dy
        if (b >= 0) {
            ty_min = (yMin - oy) * b
            ty_max = (yMax - oy) * b
        }
        else {
            ty_min = (yMax - oy) * b
            ty_max = (yMin - oy) * b
        }

        val c = 1.0 / dz
        if (c >= 0) {
            tz_min = (zMin - oz) * c
            tz_max = (zMax - oz) * c
        }
        else {
            tz_min = (zMax - oz) * c
            tz_max = (zMin - oz) * c
        }

        var t0 = 0.0; var t1 = 0.0

        // find largest entering t value
        if (tx_min > ty_min)
            t0 = tx_min
        else
            t0 = ty_min

        if (tz_min > t0)
            t0 = tz_min

        // find smallest exiting t value
        if (tx_max < ty_max)
            t1 = tx_max
        else
            t1 = ty_max

        if (tz_max < t1)
            t1 = tz_max

        return (t0 < t1 && t1 > K_EPSILON)
    }

}
