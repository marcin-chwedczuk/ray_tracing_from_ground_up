package mc.raytracer.sampling

import mc.raytracer.math.PI
import mc.raytracer.math.Point2D
import mc.raytracer.math.Point3D
import java.lang.Math.*

class HemisphereSampler private constructor(
        squareSampler: SquareSampler,
        val cosineDistribution: Double)
    : BaseSampler(squareSampler.numberOfSamples, squareSampler.numberOfSets) {

    var samples: ArrayList<Point3D> =
            mapSamplesToHemisphere(squareSampler.getSquareSamples())

    fun nextSample()
            = samples[nextShuffledIndex()]

    private fun  mapSamplesToHemisphere(squareSamples: List<Point2D>): ArrayList<Point3D> {
        val hemisphereSamples: ArrayList<Point3D> = ArrayList(squareSamples.size)

        for (squareSample in squareSamples) {
            val cos_phi = cos(2.0*PI * squareSample.x)
            val sin_phi = sin(2.0*PI * squareSample.x)
            val cos_theta = pow((1.0 - squareSample.y), 1.0/(cosineDistribution+1.0))
            val sin_theta = sqrt (1.0 - cos_theta*cos_theta)
            val pu = sin_theta * cos_phi
            val pv = sin_theta * sin_phi
            val pw = cos_theta

            hemisphereSamples.add(Point3D(pu, pv, pw));
        }

        return hemisphereSamples
    }

    companion object {
        fun fromSquareSampler(s: SquareSampler, cosineDistribution: Double = 1.0)
            = HemisphereSampler(s, cosineDistribution)
    }
}
