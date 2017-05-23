package mc.raytracer.sampling

import mc.raytracer.math.PI
import mc.raytracer.math.Point2D

class CircleSampler private constructor(
        squareSampler: SquareSampler)

    : BaseSampler(
        squareSampler.numberOfSamples,
        squareSampler.numberOfSets) {

    var circleSamples: ArrayList<Point2D> =
            mapSamplesToUnitDisk(squareSampler.getSquareSamples())

    fun nextSample()
        = circleSamples[nextShuffledIndex()]

    private fun mapSamplesToUnitDisk(squareSamples: List<Point2D>): ArrayList<Point2D> {
        val circleSamples = ArrayList<Point2D>(squareSamples.size)

        for (squareSample in squareSamples) {
            // map sample point to [-1, 1] X [-1,1]
            val x = 2.0 * squareSample.x - 1.0
            val y = 2.0 * squareSample.y - 1.0

            var r: Double
            var phi: Double

            if (x > -y) {			// sectors 1 and 2
                if (x > y) {		// sector 1
                    r = x
                    phi = y/x
                }
                else {				// sector 2
                    r = y
                    phi = 2 - x/y
                }
            }
            else {					// sectors 3 and 4
                if (x < y) {		// sector 3
                    r = -x
                    phi = 4 + y/x
                }
                else {				// sector 4
                    r = -y
                    if (y != 0.0)	// avoid division by zero at origin
                        phi = 6 - x/y
                    else
                        phi  = 0.0
                }
            }

            phi *= PI /4.0

            val circleSample = Point2D(r* Math.cos(phi), r* Math.sin(phi))
            circleSamples.add(circleSample)
        }

        return circleSamples
    }

    companion object {
       fun fromSquareSampler(s: SquareSampler) =
               CircleSampler(s)
    }
}
