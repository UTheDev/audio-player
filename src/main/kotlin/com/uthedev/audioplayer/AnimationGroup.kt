package com.uthedev.audioplayer
import javafx.animation.Animation
import javafx.animation.Transition
import javafx.util.Duration

/**
 * This class serves as a utility for handling multiple instances of [javafx.animation.Animation] at once.
 *
 * @param T The type of the keys assigned to each animation held by an instance of AnimationGroup.
 *
 * @author UTheDev
 */
class AnimationGroup<T> {
    /**
     * The map of animations currently being handled
     */
    private val map = HashMap<T, Animation>()

    /**
     * Removes an animation from the group under the given key
     *
     * @param key The key
     */
    fun remove(key: T) {
        map.remove(key)
    }

    /**
     * Adds an animation to the group under the given key
     *
     * @param key The key
     * @param anim The animation
     */
    fun add(key: T, anim: Animation) {
        map[key] = anim
    }

    /**
     * Stops all the currently registered animations
     */
    fun stopAll() {
        for ((_, anim) in map) {
            anim.stop()
        }
    }

    /**
     * Plays all the currently registered animations
     */
    fun playAll() {
        for ((_, anim) in map) {
            anim.play()
        }
    }

    /**
     * Constructs a new animation and registers it.
     *
     * @param key The key to register the animation with
     * @param duration How long the animation lasts in seconds from beginning to end
     * @param interpolateFunc The interpolation function that gets called every step of the created animation
     *
     * @return The Animation created in the process
     */
    fun addNew(key: T, duration: Double, interpolateFunc: (Double) -> Unit): Animation {
        // reminder that "Unit" is the Kotlin equivalent of Java's "void"
        val anim: Animation = object : Transition() {
            init {
                cycleDuration = Duration.seconds(duration)
            }

            override fun interpolate(frac: Double) {
                interpolateFunc(frac)
            }
        }
        
        add(key, anim)

        return anim
    }
}