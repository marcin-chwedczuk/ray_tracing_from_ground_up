package mc.raytracer.util

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Matrix4
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import java.lang.Math.max
import java.lang.Math.min

class BoundingBox(
        val xMin: Double, val xMax: Double,
        val yMin: Double, val yMax: Double,
        val zMin: Double, val zMax: Double
) {
    init {
        if (xMin > xMax) throw IllegalArgumentException("xMin cannot be greater than xMax")
        if (yMin > yMax) throw IllegalArgumentException("yMin cannot be greater than yMax")
        if (zMin > zMax) throw IllegalArgumentException("zMin cannot be greater than zMax")
    }

    public val dx = xMax - xMin
    public val dy = yMax - yMin
    public val dz = zMax - zMin

    fun isIntersecting(ray: Ray): Boolean {
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

        // find largest entering t value
        var t0 = max(tx_min, max(ty_min, tz_min))

        // find smallest exiting t value
        var t1 = min(tx_max, min(ty_max, tz_max))

        return (t0 < t1 && t1 > K_EPSILON)
    }

    fun isInside(point: Point3D): Boolean {
        return (point.x in xMin..xMax) &&
               (point.y in yMin..yMax) &&
               (point.z in zMin..zMax)
    }

    fun merge(other: BoundingBox): BoundingBox {
        if (other === EMPTY) return this
        if (this === EMPTY) return other

        return BoundingBox(
                min(xMin, other.xMin), max(xMax, other.xMax),
                min(yMin, other.yMin), max(yMax, other.yMax),
                min(zMin, other.zMin), max(zMax, other.zMax))
    }

    fun computeVertices(): List<Point3D> =
            listOf(
                    Point3D(xMin, yMin, zMin),
                    Point3D(xMax, yMin, zMin),
                    Point3D(xMax, yMin, zMax),
                    Point3D(xMin, yMin, zMax),

                    Point3D(xMin, yMax, zMin),
                    Point3D(xMax, yMax, zMin),
                    Point3D(xMax, yMax, zMax),
                    Point3D(xMin, yMax, zMax))

    fun transform(matrix: Matrix4): BoundingBox {
        val transformedVertices = computeVertices()
                .map { point -> matrix*point }

        return containingPoints(transformedVertices)
    }

    companion object {
        private val DELTA = 1e-6
        private val DELTA_VECTOR = Vector3D(DELTA, DELTA, DELTA)

        public val INFINITE = BoundingBox(
                Double.MIN_VALUE, Double.MAX_VALUE,
                Double.MIN_VALUE, Double.MAX_VALUE,
                Double.MIN_VALUE, Double.MAX_VALUE)

        public val EMPTY = BoundingBox(
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0)

        public fun containingPoints(vararg points: Point3D): BoundingBox {
            return containingPoints(points.toList())
        }

        public fun containingPoints(points: List<Point3D>): BoundingBox {
            val minPoint = Point3D.min(points)
            val maxPoint = Point3D.max(points)

            return fromMinMaxPoints(minPoint, maxPoint)
        }

        public fun fromMinMaxPoints(minPoint: Point3D, maxPoint: Point3D): BoundingBox {
            return BoundingBox(
                    minPoint.x, maxPoint.x,
                    minPoint.y, maxPoint.y,
                    minPoint.z, maxPoint.z)
        }
    }


}
