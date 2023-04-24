package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue
import javafx.animation.Animation
import javafx.animation.Transition
import javafx.util.Duration

/**
 * This class is responsible for smoothly transitioning between two Sounds
 * by transitioning their volumes.
 *
 * This class assumes that the polyphony of both the "old" and "new" AudioCues are 1.
 *
 * Based on Softlocked's SoundFader implementation.
 *
 * @author UTheDev
 *
 * @param initTargetVolume The initial target volume of the SoundFader
 */
class SoundFader(initTargetVolume: Double) {
    companion object {
        /**
         * Inverse function of LERP (linear interpolation)
         *
         * @param start The starting value
         * @param end The ending value
         * @param x The value to gauge the alpha time from
         */
        fun inverseLerp(start: Double, end: Double, x: Double): Double {
            return (x - start) / (end - start)
        }

        fun lerp(start: Double, end: Double, time: Double): Double {
            return start + (end - start) * time
        }
    }

    /**
     * The list of tweens (transitions) that are currently reducing the volume of corresponding Sounds to 0.
     *
     * This is intended to store fading in a queue in case another sound is queued for transition while a fade is in progress.
     *
     * If the fade is requested to be reversed, the sound that fades back in will be at the end of the list.
     */
    private val fadeOutTweens = AnimationGroup<Sound>()

    /**
     * The tween that's fading the current sound in
     */
    private var fadeInTween: Transition? = null

    /**
     * The current Sound
     */
    private var currentSound: Sound? = null

    private var targetVolume = initTargetVolume

    /**
     * Volume transitioning time in seconds
     */
    var transitionTime = 2.0

    /*
    private val transition = object : Transition() {
        init {
            cycleDuration = Duration.millis(2000.0)
            setOnFinished {
                if (isReversing()) {
                    onFullReverse()
                } else {
                    onFinish()
                }
            }
        }

        override fun interpolate(frac: Double) {
            old.setVolume(0, targetVolume * (1 - frac))
            new.setVolume(0, targetVolume * frac)
        }
    }
    */

    private fun stopNewSoundTween() {
        if (fadeInTween != null) {
            fadeInTween!!.pause()
            fadeInTween = null
        }
    }

    fun getTargetVolume(): Double {
        return targetVolume
    }

    /**
     * Fades to the provided Sound
     *
     * @param sound The Sound to switch to
     */
    fun switch(sound: Sound?) {
        /**
         * If the new sound is being faded out, stop that tween
         */
        if (sound != null) {
            fadeOutTweens.remove(sound)?.pause()
        }

        stopNewSoundTween()

        /*
        Fade out the old
         */
        val oldSound = currentSound
        if (oldSound != null) {
            val instId = oldSound.instanceId
            val originalVolume = oldSound.audioCue.getVolume(instId)
            val fadeOut = fadeOutTweens.addNew(oldSound, transitionTime, fun(frac: Double) {
                oldSound.audioCue.setVolume(instId, targetVolume * (1 - frac))
            })
            fadeOut.setOnFinished {
                // make sure that the animation actually finished before stopping
                if (fadeOut.currentTime.toSeconds() >= fadeOut.cycleDuration.toSeconds()) {
                    oldSound.audioCue.stop(oldSound.instanceId)
                }
            }

            // animation directions can change before the corresponding fade-in/fade-out can change, so account for that
            fadeOut.jumpTo(Duration.seconds(
                lerp(
                    0.0,
                    fadeOut.cycleDuration.toSeconds(),
                    inverseLerp(0.0, targetVolume, originalVolume)
                )
            ))

            fadeOut.play()
        }

        /*
        Fade in the new
         */
        currentSound = sound
        onSoundChange(sound)

        if (sound != null) {
            val inTween = object : Transition() {
                init {
                    cycleDuration = Duration.seconds(transitionTime)
                }

                override fun interpolate(frac: Double) {
                    sound.audioCue.setVolume(sound.instanceId, targetVolume * frac)
                }
            }

            fadeInTween = inTween

            // animation directions can change before the corresponding fade-in/fade-out can change, so account for that
            inTween.jumpTo(Duration.seconds(
                lerp(
                    0.0,
                    inTween.cycleDuration.toSeconds(),
                    inverseLerp(0.0, targetVolume, sound.audioCue.getVolume(
                        sound.instanceId
                    ))
                )
            ))
            inTween.play()
        }
    }

    /**
     * Called when the transition has been fully reversed after already being played
     */
    fun onFullReverse() {}

    /**
     * Called when the transition has finished
     */
    fun onFinish() {}

    /**
     * Fires when the primary Sound instance has changed.
     *
     * @param newSound The new primary Sound instance
     */
    fun onSoundChange(newSound: Sound?) {}
}