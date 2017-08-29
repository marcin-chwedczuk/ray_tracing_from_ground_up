package mc.raytracer.geometry.compound

import mc.raytracer.geometry.primitives.d2.Annulus
import mc.raytracer.geometry.primitives.d3.part.ConcavePartSphere
import mc.raytracer.geometry.primitives.d3.part.ConvexPartSphere
import mc.raytracer.geometry.primitives.d3.part.ConvexPartTorus
import mc.raytracer.math.Angle
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public class RoundRimmedBowl(
    val outerRadius: Double,
    val rimSize: Double
): BaseCompoundGeometricObject() {

    init {
        // outside
        addObject(ConvexPartSphere(
                outerRadius,
                Angle.fromDegrees(90), Angle.DEG_180,
                Angle.ZERO, Angle.DEG_360))

        // rim
        addObject(ConvexPartTorus(
                outerRadius - rimSize/2.0, rimSize/2.0,
                Angle.ZERO, Angle.DEG_360,
                Angle.ZERO, Angle.DEG_180))

        // inside
        addObject(ConcavePartSphere(
                outerRadius - rimSize,
                Angle.fromDegrees(90), Angle.DEG_180,
                Angle.ZERO, Angle.DEG_360))
    }
}