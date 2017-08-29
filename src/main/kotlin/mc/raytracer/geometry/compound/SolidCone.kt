package mc.raytracer.geometry.compound

import mc.raytracer.geometry.primitives.d2.Disc
import mc.raytracer.geometry.primitives.d3.OpenCone
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public class SolidCone(
        val height: Double,
        val radius: Double
) : BaseCompoundGeometricObject() {

    init {
        addObject(OpenCone(height, radius))
        addObject(Disc(Point3D.zero, -Normal3D.axisY, radius))
    }
}