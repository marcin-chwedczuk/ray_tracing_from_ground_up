package mc.raytracer.math

import org.junit.jupiter.api.fail

fun assertEquals(expected: Double, actual: Double,
                 epsilon: Double = 1e-8, message: String = "") {
    if (Math.abs(expected-actual) > epsilon) {
        val customMessage =
            if (message.isEmpty())
                ""
            else
                message + ": "

        fail("${customMessage}Values are not equal. " +
            "Expected ${expected}, actual: ${actual}, epsilon: ${epsilon}.")
    }
}