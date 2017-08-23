package mc.raytracer.lighting

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.SupportsSurfaceSampling
import mc.raytracer.material.EmissiveMaterial
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class AreaLight<out T>(val geometricObject: T)
    : Light
    where
        T: GeometricObject,
        T: SupportsSurfaceSampling
{
    private val material = (geometricObject.material as EmissiveMaterial)
    public override var generatesShadows: Boolean = true

    override fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo): HitPointLightingAttributes {
        val samplePoint = geometricObject.selectSamplePoint()
        val normalAtSamplePoint = geometricObject.normalAtSamplePoint(samplePoint)
        val wi = (samplePoint - shadingInfo.hitPoint).norm()

        return object : HitPointLightingAttributes {
            override val toLightDirection: Vector3D
                get() = wi

            override fun isHitPointInShadow(shadowRay: Ray): Boolean {
                return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay)
            }

            override fun radiance(): RgbColor {
                // Light is emitted only in surface normal direction
                // (in other words light is emitted only from one side of e.g. glowing rectangle)

                if ((-wi dot normalAtSamplePoint) < 0.0)
                    return RgbColor.black

                return material.getRadiance()
            }

            override fun samplePointGeometricFactor(): Double {
                val ndotd = -wi dot normalAtSamplePoint
                val d2 	= samplePoint.distanceToSquared(shadingInfo.hitPoint)

                val result = (ndotd / d2)
                return result
            }

            override fun samplePointPdf(): Double {
                return geometricObject.pdfOfSamplePoint(samplePoint)
            }
        }
    }
}