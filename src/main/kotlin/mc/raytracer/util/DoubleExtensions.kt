package mc.raytracer.util

fun Double.clamp(min: Double, max: Double): Double =
        if (this < min) min else
        if (this > max) max else
        this

fun Double.clampToInt(min: Int, max: Int): Int {
    val intValue = this.toInt()

    if (intValue < min) return min
    if (intValue > max) return max

    return intValue
}
