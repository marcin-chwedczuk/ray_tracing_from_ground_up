package mc.raytracer.geometry.compound.beveled

import mc.raytracer.geometry.compound.BaseCompoundGeometricObject
import mc.raytracer.geometry.primitives.d2.Disc
import mc.raytracer.geometry.primitives.d3.OpenCylinder
import mc.raytracer.geometry.primitives.d3.Torus
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.util.BoundingBox

public class BeveledCylinder(
        val yBottom: Double,
        val yTop: Double,
        val radius: Double,
        val bevelRadius: Double
): BaseCompoundGeometricObject() {

    init {
        val torus = Torus(radius - bevelRadius, bevelRadius)

        // bottom
        addObject(Disc(
                Point3D(0.0, yBottom, 0.0),
                -Normal3D.axisY,
                radius - bevelRadius))

        addObject(torus.newInstance()
                .translate(dy = yBottom + bevelRadius)
                .create())

        // side
        addObject(OpenCylinder(yBottom + bevelRadius, yTop - bevelRadius, radius))

        // top
        addObject(Disc(
                Point3D(0.0, yTop, 0.0),
                Normal3D.axisY,
                radius - bevelRadius))

        addObject(torus.newInstance()
                .translate(dy = yTop - bevelRadius)
                .create())

    }
}
