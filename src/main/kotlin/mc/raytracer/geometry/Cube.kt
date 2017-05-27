package mc.raytracer.geometry

import mc.raytracer.material.Material
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D

class Cube(
        val center: Point3D,
        val size: Double = 40.0)
    : GeometricObject() {

    private val walls: ArrayList<Rectangle2D> = ArrayList()

    override var material: Material
        get() = super.material
        set(value) {
            for (wall in walls)
                wall.material = value
            super.material = value
        }

    init {
        val x = Vector3D.axisX*size
        val y = Vector3D.axisY*size
        val z = Vector3D.axisZ*size

        val s2 = size / 2

        val top = Rectangle2D(Point3D(-s2,s2,-s2),z,x)
        val bottom = Rectangle2D(Point3D(-s2,-s2,-s2),z,x)

        val front = Rectangle2D(Point3D(-s2,-s2,s2),x,y)
        val back = Rectangle2D(Point3D(-s2,-s2,-s2),x,y)

        val left = Rectangle2D(Point3D(-s2,-s2,-s2),z,y)
        val right = Rectangle2D(Point3D(s2,-s2,-s2),z,y)

        walls.addAll(sequenceOf(top,bottom,front,back,left,right))
    }

    override fun hit(ray: Ray): HitResult {
        var tmin = Double.MAX_VALUE
        var result: HitResult = Miss.instance

        for (wall in walls) {
            val tmp = wall.hit(ray)
            if ((tmp is Hit) && tmp.tmin < tmin) {
                result = tmp
                tmin = tmp.tmin
            }
        }

        return result
    }

}
