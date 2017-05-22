package mc.raytracer.math

class Point2D(val x: Double, val y: Double) {
    constructor(x: Int, y: Int)
        : this(x.toDouble(), y.toDouble())

    fun changeX(newX: Double)
        = Point2D(newX, y)

    fun changeY(newY: Double)
        = Point2D(x, newY)

    override fun toString()
        = "point2($x, $y)"

    // operators ------------------------------

    operator fun times(scalar: Double)
        = Point2D(scalar*x, scalar*y)

    // companion object -----------------------

    companion object {
        val zero = Point2D(0,0)

        fun exchangeXCoordinates(point1: Point2D, point2: Point2D): Pair<Point2D,Point2D> {
            val newPoint1 = Point2D(point2.x, point1.y)
            val newPoint2 = Point2D(point1.x, point2.y)

            return Pair(newPoint1, newPoint2)
        }

        fun exchangeYCoordinates(point1: Point2D, point2: Point2D): Pair<Point2D,Point2D> {
            val newPoint1 = Point2D(point1.x, point2.y)
            val newPoint2 = Point2D(point2.x, point1.y)

            return Pair(newPoint1, newPoint2)
        }
    }
}

operator fun Double.times(point: Point2D)
    = Point2D(this*point.x, this*point.y)
