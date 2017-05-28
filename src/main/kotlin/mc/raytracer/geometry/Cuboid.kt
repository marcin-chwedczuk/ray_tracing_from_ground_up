package mc.raytracer.geometry

import mc.raytracer.material.Material
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D

class Cuboid(
        val center: Point3D,
        val height: Double = 40.0,
        val length: Double = 40.0,
        val depth: Double = 40.0)
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
        val x = Vector3D.axisX * length
        val y = Vector3D.axisY * height
        val z = Vector3D.axisZ * depth

        val l2 = length / 2
        val h2 = height / 2
        val d2 = depth / 2

        // length x depth
        val top = Rectangle2D(center+ Vector3D(-l2,h2,-d2),z,x)
        val bottom = Rectangle2D(center+ Vector3D(-l2,-h2,-d2),z,x)

        // length x height
        val front = Rectangle2D(center+ Vector3D(-l2,-h2,d2),x,y)
        val back = Rectangle2D(center+ Vector3D(-l2,-h2,-d2),x,y)

        // depth x height
        val left = Rectangle2D(center+ Vector3D(-l2,-h2,-d2),z,y)
        val right = Rectangle2D(center+Vector3D(l2,-h2,-d2),z,y)

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

    companion object {
        fun cube(center: Point3D, sideLength: Double)
            = Cuboid(center, sideLength, sideLength, sideLength)
    }
}
