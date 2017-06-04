package mc.raytracer.threading

import java.util.concurrent.atomic.AtomicBoolean

class CancelFlag {
    private val flag = AtomicBoolean(false)

    val shouldCancel
        get() = flag.get()

    fun raise() {
        flag.set(true)
    }

    fun clear() {
        flag.set(false)
    }
}
