package mc.raytracer.math

class Matrix4(
       private val m: DoubleArray
) {

    init {
        if (m.size != MATRIX_ELEMENTS_COUNT)
            throw IllegalArgumentException("Invalid matrix size.")
    }

    constructor(
            m00: Double = 0.0, m01: Double = 0.0, m02: Double = 0.0, m03: Double = 0.0,
            m10: Double = 0.0, m11: Double = 0.0, m12: Double = 0.0, m13: Double = 0.0,
            m20: Double = 0.0, m21: Double = 0.0, m22: Double = 0.0, m23: Double = 0.0,
            m30: Double = 0.0, m31: Double = 0.0, m32: Double = 0.0, m33: Double = 0.0)
     :   this(doubleArrayOf(
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33))

    override fun toString() =
        """[%.3f, %.3f, %.3f, %.3f
           |%.3f, %.3f, %.3f, %.3f,
           |%.3f, %.3f, %.3f, %.3f,
           |%.3f, %.3f, %.3f, %.3f]"""
                    .trimMargin()
                    .format(*m.toTypedArray())

    operator fun get(row: Int, col: Int)
            = m[row * 4 + col]


    operator fun div(scalar: Double): Matrix4 {
        val copy = DoubleArray(MATRIX_ELEMENTS_COUNT)

        for (i in 0..MATRIX_ELEMENTS_COUNT)
            copy[i] = m[i] / scalar

        return Matrix4(copy)
    }

    operator fun times(scalar: Double): Matrix4 {
        val copy = DoubleArray(MATRIX_ELEMENTS_COUNT)

        for (i in 0..MATRIX_ELEMENTS_COUNT)
            copy[i] = m[i] * scalar

        return Matrix4(copy)
    }

    operator fun times(other: Matrix4): Matrix4 {
        val copy = DoubleArray(MATRIX_ELEMENTS_COUNT)

        for (row in 0..3) {
            for (col in 0..3) {
                var sum = 0.0

                for (j in 0..3)
                    sum += this[row, j] * other[j, col]

                copy[row*4 + col] = sum
            }
        }

        return Matrix4(copy)
    }

    operator fun times(point: Point3D): Point3D
        = Point3D(
            this[0,0]*point.x + this[0,1]*point.y + this[0,2]*point.z + this[0,3],
            this[1,0]*point.x + this[1,1]*point.y + this[1,2]*point.z + this[1,3],
            this[2,0]*point.x + this[2,1]*point.y + this[2,2]*point.z + this[2,3])

    operator fun times(vec: Vector3D)
        = Vector3D(
            this[0,0]*vec.x + this[0,1]*vec.y + this[0,2]*vec.z,
            this[1,0]*vec.x + this[1,1]*vec.y + this[1,2]*vec.z,
            this[2,0]*vec.x + this[2,1]*vec.y + this[2,2]*vec.z)

    /**
     * We use equality {@code (transposed matrix)*normal = transformed_normal}.
     */
    fun transformNormal(n: Normal3D)
         = Normal3D(
            this[0,0] * n.x + this[1,0] * n.y + this[2,0] * n.z,
            this[0,1] * n.x + this[1,1] * n.y + this[2,1] * n.z,
            this[0,2] * n.x + this[1,2] * n.y + this[2,2] * n.z)

    companion object {
        val MATRIX_ELEMENTS_COUNT = 16

        val IDENTITY = Matrix4(doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        ))

        val ZERO = Matrix4(doubleArrayOf(
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0
        ))

        fun scale(xScale: Double, yScale: Double, zScale: Double): Matrix4 {
            return Matrix4(
                    xScale, 0.0, 0.0, 0.0,
                    0.0, yScale, 0.0, 0.0,
                    0.0, 0.0, zScale, 0.0,
                    0.0, 0.0, 0.0,    1.0)
        }

        fun scaleInverse(xScale: Double, yScale: Double, zScale: Double): Matrix4 {
            return Matrix4(
                    1.0/xScale, 0.0, 0.0, 0.0,
                    0.0, 1.0/yScale, 0.0, 0.0,
                    0.0, 0.0, 1.0/zScale, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun translate(dx: Double, dy: Double, dz: Double): Matrix4 {
            return Matrix4(
                    1.0, 0.0, 0.0, dx,
                    0.0, 1.0, 0.0, dy,
                    0.0, 0.0, 1.0, dz,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun translateInverse(dx: Double, dy: Double, dz: Double): Matrix4 {
            return Matrix4(
                    1.0, 0.0, 0.0, -dx,
                    0.0, 1.0, 0.0, -dy,
                    0.0, 0.0, 1.0, -dz,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateX(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    1.0, 0.0, 0.0, 0.0,
                    0.0, cos, -sin, 0.0,
                    0.0, sin, cos, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateXInverse(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    1.0, 0.0, 0.0, 0.0,
                    0.0, cos, sin, 0.0,
                    0.0, -sin, cos, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateY(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    cos, 0.0, sin, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    -sin, 0.0, cos, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateYInverse(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    cos, 0.0, -sin, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    sin, 0.0, cos, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateZ(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    cos, -sin, 0.0, 0.0,
                    sin, cos, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun rotateZInverse(angle: Angle): Matrix4 {
            val sin = angle.sin()
            val cos = angle.cos()

            return Matrix4(
                    cos, sin, 0.0, 0.0,
                    -sin, cos, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        fun shear(
            hyx: Double = 0.0, hzx: Double = 0.0,
            hxy: Double = 0.0, hzy: Double = 0.0,
            hxz: Double = 0.0, hyz: Double = 0.0
        ) : Matrix4 {
            return Matrix4(
                    1.0, hyx, hzx, 0.0,
                    hxy, 1.0, hzy, 0.0,
                    hxz, hyz, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0)
        }

        /**
         * Returns inverse of the general shearing matrix:
         * |   1 hyx hzx   0 |
         * | hxy   1 hzy   0 |
         * | hxz hyz   1   0 |
         * |   0   0   0   1 |
         */
        fun shearInverse(
                hyx: Double = 0.0, hzx: Double = 0.0,
                hxy: Double = 0.0, hzy: Double = 0.0,
                hxz: Double = 0.0, hyz: Double = 0.0
        ) : Matrix4 {

            // determinant of the shearing matrix
            val D = 1.0 - hxy*hyx - hxz*hzx - hyz*hzy +
                    hxy*hyz*hzx + hxz*hyx*hzy

            val matrix = Matrix4(
                    1.0-hyz*hzy, -hyx+hyz*hzx, -hzx+hyx*hzy, 0.0,
                    -hxy+hxz*hzy, 1.0-hxz*hzx, -hzy+hxy*hzx, 0.0,
                    -hxz+hxy*hyz, -hyz+hxz*hyx, 1.0-hxy*hyx, 0.0,
                    0.0, 0.0, 0.0, D)

            return matrix * (1.0 / D)
        }

        fun rotate(axis: Vector3D, angle: Angle): Matrix4 {
            // derived using formula: https://en.wikipedia.org/wiki/Rotation_matrix

            // @formatter:off
            val angleRadians = angle.toRadians()
            val u = axis.norm()
            val x = u.x; val y = u.y; val z = u.z

            val sin = Math.sin(angleRadians); val cos = Math.cos(angleRadians)

            return Matrix4(doubleArrayOf(
                x*x*(1-cos)+cos,   x*y*(1-cos)-z*sin, x*z*(1-cos)+y*sin, 0.0,
                y*x*(1-cos)+z*sin, y*y*(1-cos)+cos,   y*z*(1-cos)-x*sin, 0.0,
                z*x*(1-cos)-y*sin, z*y*(1-cos)+x*sin, z*z*(1-cos)+cos,   0.0,
                0.0,               0.0,               0.0,               0.0
            ))
            // @formatter:on
        }
    }
}
