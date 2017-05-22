package mc.raytracer.util

fun Double.clamp(min: Double, max: Double): Double =
        if (this < min) min else
        if (this > max) max else
        this
