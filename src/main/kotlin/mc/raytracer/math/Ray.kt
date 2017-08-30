package mc.raytracer.math

import java.util.zip.DeflaterOutputStream

class Ray private constructor(
    val origin: Point3D,
    val direction: Vector3D)
{
    // info: for transformation to work we must allow direction to
    // be non unit vector (e.g. when we scale object).
    // Light computations requires that direction is always a unit vector,
    // but this is done after transformation so transformed ray is not used.

    fun pointOnRayPath(t: Double): Point3D {
        return origin + direction*t
    }

    fun distanceToPointOnPath(point: Point3D): Double {
        return (point - origin) dot direction
    }

    fun transform(matrix: Matrix4): Ray {
        val newOrigin = matrix*origin
        val newDirection = matrix*direction

        return Ray(newOrigin, newDirection)
    }

    companion object {
        fun create(origin: Point3D, direction: Vector3D)
            = Ray(origin, direction.norm())
    }
}
