package mc.raytracer.math

import java.util.*

class Normal3D(x: Double, y: Double, z: Double) {
    public val x: Double
    public val y: Double
    public val z: Double

    init {
        val len = Math.sqrt(x*x + y*y + z*z)

        this.x = x / len
        this.y = y / len
        this.z = z / len
    }

    constructor(x: Int, y: Int, z: Int)
        : this(x.toDouble(), y.toDouble(), z.toDouble())

    private constructor(vec: Vector3D)
        : this(vec.x, vec.y, vec.z)

    val length: Double
        get() = Math.sqrt(x*x + y*y + z*z)

    infix fun dot(other: Vector3D)
        = x*other.x + y*other.y + z*other.z

    infix fun cross(other: Vector3D)
            = Vector3D(
            y*other.z - z*other.y,
            z*other.x - x*other.z,
            x*other.y - y*other.x)

    infix fun cross(other: Normal3D)
            = this cross Vector3D(other)

    override fun toString()
        = "normal3(%.3f, %.3f, %.3f)".format(x,y,z)

    // custom operators --------------------------------

    operator fun unaryMinus()
        = Normal3D(-x, -y, -z)

    operator fun plus(other: Normal3D)
        = Vector3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun plus(other: Vector3D)
        = Vector3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun times(scalar: Double)
        = Vector3D(x*scalar, y*scalar, z*scalar)

    // companion object --------------------------------------

    companion object {
        fun fromVector(vec: Vector3D) = Normal3D(vec)

        val axisY = Normal3D(0,1,0)
    }
}

operator fun Double.times(vec: Normal3D)
    = Vector3D(
        this*vec.x,
        this*vec.y,
        this*vec.z)
