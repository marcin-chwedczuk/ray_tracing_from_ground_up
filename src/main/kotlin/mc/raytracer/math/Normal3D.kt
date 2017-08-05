package mc.raytracer.math

import java.util.*

class Normal3D(val x: Double, val y:Double, val z:Double) {
    constructor(x: Int, y: Int, z: Int)
        : this(x.toDouble(), y.toDouble(), z.toDouble())

    private constructor(vec: Vector3D)
        : this(vec.x, vec.y, vec.z)

    val length: Double
        get() = Math.sqrt(x*x + y*y + z*z)

    fun dot(other: Vector3D)
        = x*other.x + y*other.y + z*other.z

    fun norm(): Normal3D {
        val len = length
        return Normal3D(x/len, y/len, z/len)
    }

    override fun toString()
        = "normal3(%.3f, %.3f, %.3f)".format(x,y,z)

    // custom operators --------------------------------

    operator fun unaryMinus()
        = Normal3D(-x, -y, -z)

    operator fun plus(other: Normal3D)
        = Normal3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun plus(other: Vector3D)
        = Vector3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun times(scalar: Double)
        = Normal3D(x*scalar, y*scalar, z*scalar)

    // companion object --------------------------------------

    companion object {
        fun fromVector(vec: Vector3D)
            = Normal3D(vec.norm())

        val axisY = Normal3D(0,1,0)
    }
}

operator fun Double.times(vec: Normal3D)
    = Normal3D(
        this*vec.x,
        this*vec.y,
        this*vec.z)
