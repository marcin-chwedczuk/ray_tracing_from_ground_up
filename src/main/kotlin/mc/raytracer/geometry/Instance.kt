package mc.raytracer.geometry

import mc.raytracer.material.Material
import mc.raytracer.math.Matrix4
import mc.raytracer.math.Ray

public class Instance(
        private val geometricObject: GeometricObject,
        private val rayTransformation: Matrix4,
        private val name: String
): GeometricObject() {

    private var customMaterial: Material? = null

    override var material: Material
        get() = customMaterial ?: geometricObject.material
        set(value) { customMaterial = value }

    override fun hit(ray: Ray): HitResult {
        val transformedRay = ray.transform(rayTransformation)
        val hitResult = geometricObject.hit(transformedRay)

        if (hitResult is Hit)
            return Hit(
                tmin = hitResult.tmin,
                normalAtHitPoint = rayTransformation.transformNormal(hitResult.normalAtHitPoint),
                localHitPoint = hitResult.localHitPoint)

        return Miss.instance
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val transformedRay = shadowRay.transform(rayTransformation)
        return geometricObject.shadowHit(transformedRay)
    }
}