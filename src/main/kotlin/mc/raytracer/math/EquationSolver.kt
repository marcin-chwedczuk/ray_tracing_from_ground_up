package mc.raytracer.math

import java.lang.Math.*

object EquationSolver {

    /**
     *  Solves equation:
     *
     *      c[0] + c[1]*x + c[2]*x^2 = 0
     *
     */
    public fun solveX2_alt(c: List<Double>): List<Double> {
        // Version from "Numerical recipes in C"

        val C = c[0]; val B = c[1]; val A = c[2]

        val delta = B*B - 4*A*C

        if (isZero(delta)) {
            return listOf(-B/(2.0*A))
        }
        else if (delta < 0.0) {
            return emptyList()
        }
        else {
            val sqrtDelta = Math.sqrt(delta)
            val q = -0.5 * (B + Math.signum(B)*sqrtDelta)

            return listOf(q/A, C/q)
        }
    }

    public fun solveX2(c: List<Double>): List<Double> {
        val p = c[ 1 ] / (2 * c[2])
        val q = c[ 0 ] / c[2]

        val D = p * p - q

        if (isZero(D)) {
            return listOf(-p)
        }

        if (D > 0) {
            val sqrt_D = sqrt(D)

            return listOf(
                    sqrt_D  - p,
                    -sqrt_D - p)

        }

        return emptyList()
    }

    /**
     *  Solves equation:
     *
     *      c[0] + c[1]*x + c[2]*x^2 + c[3]*x^3 = 0
     *
     */
    public fun solveX3_alt(c: List<Double>): List<Double> {
        // Version from "Numerical recipes in C"
        /* normal form: x^3 + Ax^2 + Bx + C = 0 */

        val a = c[ 2 ] / c[ 3 ]
        val b = c[ 1 ] / c[ 3 ]
        val c = c[ 0 ] / c[ 3 ]

        val Q = (a*a - 3*b) / 9
        val R = (2*a*a*a - 9*a*b + 27*c) / 54

        val Q3 = Q*Q*Q
        if ((R*R) < Q3) {
            val fi = Math.acos(R / Math.sqrt(Q3))
            val m = -2.0 * Math.sqrt(Q)
            val a3 = a / 3.0

            return listOf(
                    m*Math.cos((fi + 0*PI) / 3.0) - a3,
                    m*Math.cos((fi + 2*PI) / 3.0) - a3,
                    m*Math.cos((fi - 2*PI) / 3.0) - a3)
        }
        else {
            val tmp = Math.abs(R) + Math.sqrt(R*R - Q3)
            val A = -Math.signum(R)*Math.cbrt(tmp)
            val B = if (isZero(A)) 0.0 else (Q / A)

            return listOf((A+B) - a/3.0)
        }

    }

    public fun solveX3(c: List<Double>): List<Double> {
        // Version from "Graphic Gems"
        /* normal form: x^3 + Ax^2 + Bx + C = 0 */

        val A = c[ 2 ] / c[ 3 ]
        val B = c[ 1 ] / c[ 3 ]
        val C = c[ 0 ] / c[ 3 ]

        /*  substitute x = y - A/3 to eliminate quadric term:
        x^3 +px + q = 0 */

        val sq_A = A * A
        val p = 1.0/3 * (- 1.0/3 * sq_A + B)
        val q = 1.0/2 * (2.0/27 * A * sq_A - 1.0/3 * A * B + C)

        /* use Cardano's formula */

        val cb_p = p * p * p
        val D = q * q + cb_p

        val solution: List<Double>

        if (isZero(D)) {
            if (isZero(q)) { /* one triple solution */
                solution = listOf(0.0)
            }
            else { /* one single and one val solution */
                val u = cbrt(-q)
                solution = listOf(2*u, -u)
            }
        }
        else if (D < 0) {
            // Casus irreducibilis: three real solutions

            val phi = 1.0/3 * acos(-q / sqrt(-cb_p))
            val t = 2 * sqrt(-p)

            solution = listOf(
                t * cos(phi),
                -t * cos(phi + PI / 3),
                -t * cos(phi - PI / 3))
        }
        else { /* one real solution */
            val sqrt_D = sqrt(D)
            val u = cbrt(sqrt_D - q)
            val v = - cbrt(sqrt_D + q)

            solution = listOf(u+v)
        }

        // resubstitute
        val sub = 1.0/3 * A
        return solution.map { it - sub }
    }


    /**
     *  Solves equation:
     *
     *      c[0] + c[1]*x + c[2]*x^2 + c[3]*x^3 + c[4]*x^4 = 0
     *
     */
    public fun solveX4(c: List<Double>): List<Double> {
        /* normal form: x^4 + Ax^3 + Bx^2 + Cx + D = 0 */

        val A = c[ 3 ] / c[ 4 ]
        val B = c[ 2 ] / c[ 4 ]
        val C = c[ 1 ] / c[ 4 ]
        val D = c[ 0 ] / c[ 4 ]

        /*  substitute x = y - A/4 to eliminate cubic term:
        x^4 + px^2 + qx + r = 0 */

        val sq_A = A * A
        val p = - 3.0/8 * sq_A + B
        val q = 1.0/8 * sq_A * A - 1.0/2 * A * B + C
        val r = - 3.0/256*sq_A*sq_A + 1.0/16*sq_A*B - 1.0/4*A*C + D

        var solution: List<Double>

        if (isZero(r)) {
            /* no absolute term: y(y^3 + py + q) = 0 */

            val tmp = solveX3(listOf(q,p,0.0,1.0))
            solution = tmp.plus(0.0)
        }
        else {
            /* solve the resolvent cubic ... */

            val tmp = listOf(
                1.0/2 * r * p - 1.0/8 * q * q,
                (-r),
                (-1.0/2) * p,
                1.0)

            val tmpSolution = solveX3(tmp)

            /* ... and take the one real solution ... */

            val z = tmpSolution[0]

            /* ... to build two quadric equations */

            var u = z * z - r
            var v = 2 * z - p

            if (isZero(u))
                u = 0.0
            else if (u > 0)
                u = sqrt(u)
            else
                return emptyList()

            if (isZero(v))
                v = 0.0
            else if (v > 0)
                v = sqrt(v)
            else
                return emptyList()

            val sol1 = solveX2(listOf(
                    z-u,
                    if (q < 0) -v else v,
                    1.0))

            val sol2 = solveX2(listOf(
                    z+u,
                    if (q < 0) v else -v,
                    1.0))

            solution = sol1.plus(sol2)
        }

        // resubstitute
        val sub = 1.0/4 * A
        return solution.map { it-sub }
    }

    private fun isZero(value: Double): Boolean {
        val ZERO_ERROR = 1e-9
        return ((-ZERO_ERROR) <= value) && (value <= ZERO_ERROR)
    }

}

