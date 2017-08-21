package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public interface Light {
    fun computeDirectionFromHitPointToLight(
            shadingInfo: ShadingInfo): Vector3D

    fun computeLuminanceContributedByLight(
            shadingInfo: ShadingInfo): RgbColor
}