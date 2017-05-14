package mc.raytracer.math

class Point2D(val x: Double, val y: Double) {
    constructor(x: Int, y: Int)
        : this(x.toDouble(), y.toDouble())

    override fun toString()
        = "point2($x, $y)"

    // operators ------------------------------

    operator fun times(scalar: Double)
        = Point2D(scalar*x, scalar*y)
}

operator fun Double.times(point: Point2D)
    = Point2D(this*point.x, this*point.y)
