package mc.raytracer.geometry.compound

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.material.Material
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox

public open class BaseCompoundGeometricObject: GeometricObject() {
    private val geometricObjects = mutableListOf<GeometricObject>()
    private var _boundingBox = BoundingBox.EMPTY

    public val size: Int
            get() = geometricObjects.size

    public override val boundingBox: BoundingBox
            get() = _boundingBox

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
        _boundingBox = _boundingBox.merge(geometricObject.boundingBox)
    }


    override fun hit(ray: Ray): HitResult {
        if (!boundingBox.isIntersecting(ray)) {
            return Miss.instance
        }

        return geometricObjects
                .map { it.hit(ray) }
                .filterIsInstance<Hit>()
                .minByOrNull { it.tmin }
                ?: Miss.instance
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        if (!boundingBox.isIntersecting(shadowRay)) {
            return null
        }

        return geometricObjects
                .mapNotNull { it.shadowHit(shadowRay) }
                .minByOrNull { it }
    }
}