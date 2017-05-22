package mc.raytracer.util

fun Int.sqrtInt(): Int {
    val intSqrt = Math.sqrt(0.5 + this).toInt()

    if (intSqrt*intSqrt != this)
        throw IllegalArgumentException("Number $this is not a perfect square.")

    return intSqrt
}
