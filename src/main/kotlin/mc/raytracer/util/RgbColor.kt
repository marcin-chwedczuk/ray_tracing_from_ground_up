package mc.raytracer.util

class RgbColor(val r: Float, val g: Float, val b: Float) {
    constructor()
        : this(0f, 0f, 0f)

    constructor(r: Double, g: Double, b: Double)
        : this(r.toFloat(), g.toFloat(), b.toFloat())

    fun multiplyComponentwise(other: RgbColor)
        = RgbColor(r*other.r, g*other.g, b*other.b)

    fun averageComponents(): Double
        = (r+g+b)/3.0

    fun powComponents(power: Double)
        = RgbColor(
            Math.pow(r.toDouble(), power),
            Math.pow(g.toDouble(), power),
            Math.pow(b.toDouble(), power))

    fun scaleMaxToOne(): RgbColor {
        val max = Math.max(r, Math.max(g, b))
        if (max > 1.0)
            return this/max.toDouble()
        return this
    }

    fun orWhenOutOfRange(color: RgbColor): RgbColor {
        val max = Math.max(r, Math.max(g, b))
        return if (max > 1.0f) color else this
    }

    fun toInt(): Int {
        val rr = (r*255).toInt() shl 16
        val gg = (g*255).toInt() shl 8
        val bb = (b*255).toInt()

        return 0xff000000.toInt() or rr or gg or bb
    }

    override fun toString()
        = "rgb($r,$g,$b)"

    operator fun plus(other: RgbColor)
        = RgbColor(r+other.r, g+other.g, b+other.b)

    operator fun times(value: Double)
        = RgbColor(r*value, g*value, b*value)

    operator fun div(value: Double)
        = RgbColor(r/value, g/value, b/value)

    companion object {
        val white = RgbColor(1.0, 1.0, 1.0)
        val black = RgbColor(0.0, 0.0, 0.0)
        val red = RgbColor(1.0, 0.0, 0.0)
        val yellow = RgbColor(1.0,1.0,0.0)
        val green = RgbColor(0.0, 1.0, 0.0)
        val blue = RgbColor(0.0, 0.0, 1.0)

        fun grayscale(value: Double): RgbColor {
            val clamped = value.clamp(0.0, 1.0)
            return RgbColor(clamped, clamped, clamped)
        }
    }
}

operator fun Double.times(color: RgbColor)
    = RgbColor(this*color.r, this*color.g, this*color.b)

