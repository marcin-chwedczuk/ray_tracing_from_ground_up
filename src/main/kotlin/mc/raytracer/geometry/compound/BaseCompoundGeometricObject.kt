package mc.raytracer.geometry.compound

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.material.Material
import mc.raytracer.math.Ray

public open class BaseCompoundGeometricObject: GeometricObject() {
    private val geometricObjects = mutableListOf<GeometricObject>()

    override var material: Material
        get() {
            return super.material
        }
        set(value) {
            super.material = value

            for (geometricObject in geometricObjects) {
                geometricObject.material = value
            }
        }

    public fun addObject(geometricObject: GeometricObject) {
        geometricObjects.add(geometricObject)
    }

    override fun hit(ray: Ray): HitResult {
        return geometricObjects
                .map { it.hit(ray) }
                .filterIsInstance<Hit>()
                .minBy { it.tmin }
                ?: Miss.instance
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        return geometricObjects
                .mapNotNull { it.shadowHit(shadowRay) }
                .minBy { it }
    }
}