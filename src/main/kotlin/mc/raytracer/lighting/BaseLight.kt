package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.util.ShadingInfo

public interface LightWithShadowSupport : Light {
    val castsShadows: Boolean
    fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean
}
