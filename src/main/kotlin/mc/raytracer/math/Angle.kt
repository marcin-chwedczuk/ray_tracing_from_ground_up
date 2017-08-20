package mc.raytracer.math

class Angle {
    private val angleInRadians: Double

    private constructor(angleInRadians: Double) {
        this.angleInRadians = angleInRadians
    }

    fun toDegrees(): Double
        = angleInRadians.radToDeg()

    fun toRadians(): Double
        = angleInRadians

    override fun toString()
        = "(innerCutOff ${toDegrees()}\u00B0)"

    fun tan(): Double
            = Math.tan(angleInRadians)

    fun sin(): Double
            = Math.sin(angleInRadians)

    fun cos(): Double
            = Math.cos(angleInRadians)

    operator fun times(x: Int): Angle
        = this * x.toDouble()

    operator fun times(x: Double): Angle
        = Angle(angleInRadians * x)

    operator fun div(x: Int): Angle
        = this / x.toDouble()

    operator fun div(x: Double): Angle
        = Angle(angleInRadians / x)

    companion object {
        val ZERO = fromDegrees(0)

        fun fromDegrees(angleInDegrees: Int)
            = fromDegrees(angleInDegrees.toDouble())

        fun fromDegrees(angleInDegrees: Double)
            = Angle(angleInDegrees.degToRad())

        fun fromRadians(angleInRadians: Double)
            = Angle(angleInRadians)
    }
}
