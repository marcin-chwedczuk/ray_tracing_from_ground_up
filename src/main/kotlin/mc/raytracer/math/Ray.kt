package mc.raytracer.math

class Ray(
    val origin: Point3D,
    direction: Vector3D)
{
    val direction = direction.norm()
}
