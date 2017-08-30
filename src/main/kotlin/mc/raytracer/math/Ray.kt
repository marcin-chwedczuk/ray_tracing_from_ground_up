package mc.raytracer.math

import java.util.zip.DeflaterOutputStream

class Ray(
    val origin: Point3D,
    direction: Vector3D)
{
    val direction = direction.norm()

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
}
