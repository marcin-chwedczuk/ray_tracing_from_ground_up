package mc.raytracer.math

fun Double.degToRad(): Double {
    return PI_ON_180*this
}

fun Double.radToDeg(): Double {
    return this / PI_ON_180
}
