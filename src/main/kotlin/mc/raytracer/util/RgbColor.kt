package mc.raytracer.util

class RgbColor(val r: Float, val g: Float, val b: Float) {
    constructor()
        : this(0f, 0f, 0f)

    constructor(r: Double, g: Double, b: Double)
        : this(r.toFloat(), g.toFloat(), b.toFloat())

    constructor(r: Short, g: Short, b: Short)
        : this(r / 255.0, g / 255.0, b / 255.0)

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

    fun isOutOfRange(): Boolean {
        val max = Math.max(r, Math.max(g, b))
        return (max > 1.0)
    }

    fun orWhenOutOfRange(color: RgbColor): RgbColor {
        val max = Math.max(r, Math.max(g, b))
        return if (max > 1.0f) color else this
    }

    fun toArgb(): Int {
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

    operator fun times(other: RgbColor)
        = RgbColor(r*other.r, g*other.g, b*other.b)

    operator fun div(value: Double)
        = RgbColor(r/value, g/value, b/value)

    companion object {
        val white = RgbColor(1.0, 1.0, 1.0)
        val black = RgbColor(0.0, 0.0, 0.0)
        val red = RgbColor(1.0, 0.0, 0.0)
        val yellow = RgbColor(1.0,1.0,0.0)
        val orange = RgbColor(1.0, 0.529, 0.0)
        val pink = RgbColor(255, 105, 180)
        val green = RgbColor(0.0, 1.0, 0.0)
        val blue = RgbColor(0.0, 0.0, 1.0)

        fun grayscale(value: Double): RgbColor {
            val clamped = value.clamp(0.0, 1.0)
            return RgbColor(clamped, clamped, clamped)
        }

        fun randomColor()
            = RgbColor.fromHsv(GlobalRandom.nextDouble(0.0, 360.0), 1.0, 1.0)

        fun fromArgb(color: Long): RgbColor {
            val r = ((0xFF0000L and color) shr 16) / 255.0
            val g = ((0x00FF00L and color) shr  8) / 255.0
            val b = ((0x0000FFL and color) shr  0) / 255.0

            return RgbColor(r,g,b)
        }

        /**
         * Create {@code RgbColor} from HSV values.
         *
         * @param h Hue, in degrees from 0 up to 360
         * @param s Saturation from 0 to 1 (1 means full color, 0 means white)
         * @param v Value from 0 to 1 (1 means full color, 0 means black)
         */
        fun fromHsv(h: Double, s: Double, v: Double): RgbColor {
            // adapted from: https://stackoverflow.com/a/6930407/1779504

            if(s <= 0.0) {
                return RgbColor(v,v,v)
            }

            val hh = (if(h >= 360.0) 0.0 else h) / 60.0
            val i = hh.toLong()
            val ff = hh - i
            val p = v * (1.0 - s)
            val q = v * (1.0 - (s * ff))
            val t = v * (1.0 - (s * (1.0 - ff)))

            return when(i) {
                0L -> RgbColor(v,t,p)
                1L -> RgbColor(q,v,p)
                2L -> RgbColor(p,v,t)
                3L -> RgbColor(p,q,v)
                4L -> RgbColor(t,p,v)
                else -> RgbColor(v,p,q)
            }
        }
    }
}

operator fun Double.times(color: RgbColor)
    = RgbColor(this*color.r, this*color.g, this*color.b)

