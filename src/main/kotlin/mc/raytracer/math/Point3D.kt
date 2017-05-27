package mc.raytracer.math

class Point3D(val x: Double, val y: Double, val z: Double) {
    constructor(x: Int, y: Int, z: Int)
        : this(x.toDouble(), y.toDouble(), z.toDouble())

    fun distanceTo(other: Point3D) =
        Math.sqrt(
            (x-other.x)*(x-other.x) +
            (y-other.y)*(y-other.y) +
            (z-other.z)*(z-other.z))

    fun distanceToSquared(other: Point3D) =
        (x-other.x)*(x-other.x) +
        (y-other.y)*(y-other.y) +
        (z-other.z)*(z-other.z)

    override fun equals(other: Any?): Boolean {
        if (other !is Point3D)
            return false

        return equals(other)
    }

    fun equals(other: Point3D): Boolean {
        val dx = Math.abs(x - other.x)
        val dy = Math.abs(y - other.y)
        val dz = Math.abs(z - other.z)

        return (dx<EPSILON) && (dy<EPSILON) && (dz<EPSILON)
    }

    override fun toString()
        = "point3(%.3f, %.3f, %.3f)".format(x,y,z)

    // operators ----------------------------------

    operator fun plus(vec: Vector3D)
        = Point3D(
            x + vec.x,
            y + vec.y,
            z + vec.z)

    operator fun minus(vec: Vector3D)
        = Point3D(
            x - vec.x,
            y - vec.y,
            z - vec.z)

    operator fun minus(other: Point3D)
        = Vector3D(
            x - other.x,
            y - other.y,
            z - other.z)

    operator fun times(scale: Double)
        = Point3D(x*scale, y*scale, z*scale)

    // companion object ----------------------------------

    companion object {
        val zero = Point3D(0.0, 0.0, 0.0)
    }
}

operator fun Double.times(point: Point3D)
    = Point3D(point.x*this, point.y*this, point.z*this)

