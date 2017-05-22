package mc.raytracer.geometry

import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.*
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class SinX2Y2Function(val topLeft: Point2D, val bottomRight: Point2D): GeometricObject() {
    override fun hit(ray: Ray): HitResult {
        val origin = ray.origin

        // 3.79
        if (origin.x < topLeft.x || origin.x > bottomRight.x)
            return Miss.instance

        if (origin.y > topLeft.y || origin.y < bottomRight.y)
            return Miss.instance

        val s = 3.79
        val scaledX = s * (origin.x - topLeft.x) / (bottomRight.x - topLeft.x)
        val scaledY = s * (origin.y - bottomRight.y) / (topLeft.y - bottomRight.y)

        val f = 0.5 * (1 + Math.sin(scaledX*scaledX*scaledY*scaledY))

        return Hit(1.0, ShadingInfo(
            objectHit = true,
            localHitPoint = Point3D(origin.x, origin.y, f),
            normalAtHitPoint = Normal3D(0,0,1),
            material = StaticColorMaterial(RgbColor.grayscale(f))
        ))
    }
}
