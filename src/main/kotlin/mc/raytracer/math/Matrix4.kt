package mc.raytracer.math

class Matrix4 {
    val m: DoubleArray

    constructor(m: Array<Double>) : this(m.toDoubleArray())

    constructor(m: DoubleArray) {
        this.m = m
    }

    inline operator fun get(row: Int, col: Int)
            = m[row * 4 + col]

    override fun toString() =
        """[%.3f, %.3f, %.3f, %.3f
           |%.3f, %.3f, %.3f, %.3f,
           |%.3f, %.3f, %.3f, %.3f,
           |%.3f, %.3f, %.3f, %.3f]"""
                .trimMargin()
                .format(*m.toTypedArray())

    operator fun div(scalar: Double): Matrix4 {
        val copy = DoubleArray(16)

        for (i in 0..15)
            copy[i] = m[i] / scalar

        return Matrix4(copy)
    }

    operator fun times(other: Matrix4): Matrix4 {
        val copy = DoubleArray(16)

        for (y in 0..3) {
            for (x in 0..3) {
                var sum = 0.0

                for (j in 0..3)
                    sum += this[x, j] * other[j, y]

                copy[x*4 + y] = sum
            }
        }

        return Matrix4(copy)
    }

    operator fun times(vec: Vector3D)
        = Vector3D(
            this[0,0]*vec.x + this[0,1]*vec.y + this[0,2]*vec.z,
            this[1,0]*vec.x + this[1,1]*vec.y + this[1,2]*vec.z,
            this[2,0]*vec.x + this[2,1]*vec.y + this[2,2]*vec.z)

    operator fun times(normal: Normal3D)
         = Normal3D(
            this[0,0]*normal.x + this[0,1]*normal.y + this[0,2]*normal.z,
            this[1,0]*normal.x + this[1,1]*normal.y + this[1,2]*normal.z,
            this[2,0]*normal.x + this[2,1]*normal.y + this[2,2]*normal.z)

    companion object {
        val identity = Matrix4(arrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        ))

        val zero = Matrix4(arrayOf(
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0
        ))
    }
}
