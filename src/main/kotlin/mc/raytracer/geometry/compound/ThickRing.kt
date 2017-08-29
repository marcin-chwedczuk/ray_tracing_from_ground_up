package mc.raytracer.geometry.compound

import mc.raytracer.geometry.primitives.d2.Annulus
import mc.raytracer.geometry.primitives.d3.part.ConcavePartCylinder
import mc.raytracer.math.Angle
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public class ThickRing(
    val innerRadius: Double,
    val outerRadius: Double,
    val yBottom: Double,
    val yTop: Double
) : BaseCompoundGeometricObject() {

    init {
        // top
        addObject(Annulus(Point3D(0.0, yTop, 0.0), innerRadius, outerRadius, Normal3D.axisY))

        // inner side
        addObject(ConcavePartCylinder(innerRadius, yBottom, yTop, Angle.ZERO, Angle.TWO_PI_RADIANS))

        // outer side
        addObject(ConcavePartCylinder(outerRadius, yBottom, yTop, Angle.ZERO, Angle.TWO_PI_RADIANS))

        // bottom
        addObject(Annulus(Point3D(0.0, yBottom, 0.0), innerRadius, outerRadius, -Normal3D.axisY))
    }
}