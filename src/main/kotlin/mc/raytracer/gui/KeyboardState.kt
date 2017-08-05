package mc.raytracer.gui

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame

class KeyboardState {
    private val pressedKeys = mutableSetOf<KeyInfo>()

    public val hasPressedKeys
        get() = pressedKeys.isNotEmpty()

    public fun registerListener(jframe: JFrame) {
        jframe.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                pressedKeys.add(KeyInfo(e!!.keyCode, e.keyChar))
            }

            override fun keyReleased(e: KeyEvent?) {
                pressedKeys.remove(KeyInfo(e!!.keyCode, e.keyChar))
            }
        })
    }

    public fun forPressedKeys(action: (KeyInfo)->Unit) {
        // System.out.println("NUMBER OF PRESSED KEYS: " + pressedKeys.size)

        for (pressedKey in pressedKeys) {
            action.invoke(pressedKey)
        }
    }

    public class KeyInfo(
        val keyCode: Int,
        val keyChar: Char
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as KeyInfo

            if (keyCode != other.keyCode) return false

            return true
        }

        override fun hashCode(): Int {
            return keyCode
        }
    }
}