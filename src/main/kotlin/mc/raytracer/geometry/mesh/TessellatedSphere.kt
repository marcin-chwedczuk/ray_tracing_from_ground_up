package mc.raytracer.geometry.mesh

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.acceleration.AccelerationGrid
import mc.raytracer.geometry.primitives.d2.Triangle
import mc.raytracer.material.Material
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import java.lang.Math.cos
import java.lang.Math.sin

public class TessellatedSphere(
        val horizontalSteps: Int,
        val verticalSteps: Int,
        val triangleMaterial: Material
) {
    public fun <TRIANGLE : GeometricObject> addTrianglesToGridTemplate(
            grid: AccelerationGrid,
            triangleFactory: (Point3D,Point3D,Point3D)->TRIANGLE) {

        val pi = 3.1415926535897932384

        // define the top triangles which all touch the north pole
        var k = 1

        for (j in 0..(horizontalSteps - 1)) {
            val v0 = Point3D(0, 1, 0)

            val v1 = Point3D(
                    sin(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps),
                    cos(pi * k / verticalSteps),
                    cos(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps))

            val v2 = Point3D(
                    sin(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps),
                    cos(pi * k / verticalSteps),
                    cos(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps))

            grid.addObject(triangleFactory(v0, v1, v2))
        }

        // define the bottom triangles which all touch the south pole
        k = verticalSteps - 1
        for (j in 0..(horizontalSteps - 1)) {
            val v0 = Point3D(
                    sin(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps),
                    cos(pi * k / verticalSteps),
                    cos(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps))

            val v1 = Point3D(0, -1, 0)

            val v2 = Point3D(
                    sin(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps),
                    cos(pi * k / verticalSteps),
                    cos(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps))

            grid.addObject(triangleFactory(v0, v1, v2))
        }

        //  define the other triangles
        for (k in 1..(verticalSteps - 2)) {
            for (j in 0..(horizontalSteps - 1)) {

                // define the first triangle
                val v0 = Point3D(
                        sin(2.0 * pi * j / horizontalSteps) * sin(pi * (k + 1) / verticalSteps),
                        cos(pi * (k + 1) / verticalSteps),
                        cos(2.0 * pi * j / horizontalSteps) * sin(pi * (k + 1) / verticalSteps))

                val v1 = Point3D(
                        sin(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * (k + 1) / verticalSteps),
                        cos(pi * (k + 1) / verticalSteps),
                        cos(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * (k + 1) / verticalSteps))

                val v2 = Point3D(
                        sin(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps),
                        cos(pi * k / verticalSteps),
                        cos(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps))

                grid.addObject(triangleFactory(v0, v1, v2))

                // define the second triangle
                val v3 = Point3D(
                        sin(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps),
                        cos(pi * k / verticalSteps),
                        cos(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * k / verticalSteps))

                val v4 = Point3D(sin(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps),
                        cos(pi * k / verticalSteps),
                        cos(2.0 * pi * j / horizontalSteps) * sin(pi * k / verticalSteps))

                val v5 = Point3D(
                        sin(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * (k + 1) / verticalSteps),
                        cos(pi * (k + 1) / verticalSteps),
                        cos(2.0 * pi * (j + 1) / horizontalSteps) * sin(pi * (k + 1) / verticalSteps))

                grid.addObject(triangleFactory(v3, v4, v5))
            }
        }
    }

    public fun addTrianglesToGrid(grid: AccelerationGrid) {
        addTrianglesToGridTemplate(grid, { v0,v1,v2 -> flatTriangle(v0,v1,v2) })
    }

    public fun addSmoothTrianglesToGrid(grid: AccelerationGrid) {
        addTrianglesToGridTemplate(grid, { v0,v1,v2 -> smoothTriangle(v0,v1,v2) })
    }

    private fun flatTriangle(v0: Point3D, v1: Point3D, v2: Point3D): Triangle {
        return Triangle(v0, v1, v2).apply {
            material = triangleMaterial
        }
    }

    private fun smoothTriangle(v0: Point3D, v1: Point3D, v2: Point3D): TessellatedSphereSmoothTriangle {
        val n0 = Normal3D.fromVector(v0 - Point3D.zero)
        val n1 = Normal3D.fromVector(v1 - Point3D.zero)
        val n2 = Normal3D.fromVector(v2 - Point3D.zero)

        return TessellatedSphereSmoothTriangle(v0, v1, v2, n0, n1, n2).apply {
            material = triangleMaterial
        }
    }
}