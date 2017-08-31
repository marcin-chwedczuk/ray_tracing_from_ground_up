package mc.raytracer.geometry.compound

import mc.raytracer.geometry.primitives.d2.Disc
import mc.raytracer.geometry.primitives.d3.OpenCylinder
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public class SolidCylinder(
        val yBottom: Double,
        val yTop: Double,
        val radius: Double
): BaseCompoundGeometricObject() {

    init {
        // bottom
        addObject(Disc(
                Point3D(0.0,yBottom,0.0),
                -Normal3D.axisY,
                radius))

        // side
        addObject(OpenCylinder(yBottom, yTop, radius))

        // top
        addObject(Disc(
                Point3D(0.0,yTop,0.0),
                Normal3D.axisY,
                radius))
    }

}