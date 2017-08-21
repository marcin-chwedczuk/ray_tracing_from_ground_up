package mc.raytracer.math

import java.util.*

class Vector3D(val x: Double, val y:Double, val z:Double) {

    constructor(x: Int, y: Int, z: Int)
        : this(x.toDouble(), y.toDouble(), z.toDouble())

    constructor(normal: Normal3D)
        : this(normal.x, normal.y, normal.z)

    val length: Double
        get() = Math.sqrt(x*x + y*y + z*z)

    val lengthSquared: Double
        get() = x*x + y*y + z*z

    infix fun dot(other: Vector3D)
        = x*other.x + y*other.y + z*other.z

    infix fun dot(other: Normal3D)
        = x*other.x + y*other.y + z*other.z

    infix fun cross(other: Vector3D)
        = Vector3D(
            y*other.z - z*other.y,
            z*other.x - x*other.z,
            x*other.y - y*other.x)

    fun norm(): Vector3D {
        val len = length
        return Vector3D(x/len, y/len, z/len)
    }

    override fun toString()
        = "vec3(%.3f, %.3f, %.3f)".format(x,y,z)

    // custom operators --------------------------------

    operator fun unaryMinus()
        = Vector3D(-x, -y, -z)

    operator fun plus(other: Vector3D)
        = Vector3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun plus(other: Normal3D)
        = Vector3D(
            x + other.x,
            y + other.y,
            z + other.z)

    operator fun minus(other: Vector3D)
        = Vector3D(
            x - other.x,
            y - other.y,
            z - other.z)

    operator fun times(scalar: Double)
        = Vector3D(x*scalar, y*scalar, z*scalar)

    operator fun div(scalar: Double)
        = Vector3D(x/scalar, y/scalar, z/scalar)

    // companion object ---------------------------------------

    companion object {
        val zero = Vector3D(0,0,0)

        val axisX = Vector3D(1,0,0)
        val axisY = Vector3D(0,1,0)
        val axisZ = Vector3D(0,0,1)
    }
}

operator fun Double.times(vec: Vector3D)
    = Vector3D(
        this*vec.x,
        this*vec.y,
        this*vec.z)

