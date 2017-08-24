package mc.raytracer.sampling

import mc.raytracer.math.PI
import mc.raytracer.math.Point2D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D

public class UniformSphereSampler private constructor(
        squareSampler: SquareSampler)
    : BaseSampler(squareSampler.numberOfSamples, squareSampler.numberOfSets) {

    var samples: ArrayList<Point3D> =
            mapSamplesToSphere(squareSampler.getSquareSamples())

    fun nextSampleOnUnitSphere()
            = samples[nextShuffledIndex()]

    fun nextSampleVectorOnUnitSphere()
        = nextSampleOnUnitSphere() - Point3D.zero

    private fun  mapSamplesToSphere(squareSamples: List<Point2D>): ArrayList<Point3D> {
        val sphereSamples: ArrayList<Point3D> = ArrayList(squareSamples.size)

        for (squareSample in squareSamples) {
            // from: http://mathworld.wolfram.com/SpherePointPicking.html

            val u = squareSample.x*2.0 - 1.0
            val theta = 2.0 * PI * squareSample.y

            val x = Math.sqrt(1 - u*u) * Math.cos(theta)
            val y = Math.sqrt(1 - u*u) * Math.sin(theta)
            val z = u

            sphereSamples.add(Point3D(x, y, z))
        }

        return sphereSamples
    }
    companion object {
        public fun fromSquareSampler(squareSampler: SquareSampler)
            = UniformSphereSampler(squareSampler)
    }
}