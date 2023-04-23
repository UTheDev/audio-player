package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue
import javafx.animation.Transition
import javafx.util.Duration

/**
 * This class is responsible for smoothly transitioning between two AudioCues
 * by transitioning their volumes.
 *
 * This class assumes that the polyphony of both the "old" and "new" AudioCues are 1.
 *
 * Based on Softlocked's SoundFader implementation.
 *
 * @author UTheDev
 *
 * @param initTargetVolume The initial target volume of the AudioFader
 */
class AudioFader(initTargetVolume: Double) {
    /**
     * The list of tweens (transitions) that are currently reducing the volume of corresponding AudioCues to 0.
     *
     * This is intended to store fading in a queue in case another sound is queued for transition while a fade is in progress.
     *
     * If the fade is requested to be reversed, the sound that fades back in will be at the end of the list.
     */
    private val fadeOutTweens = AnimationGroup<AudioCue>()

    /**
     * The tween that's fading the current sound in
     */
    private var fadeInTween: Transition? = null

    /**
     * The current AudioCue
     */
    private var currentSound: AudioCue? = null

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
     * Fades to the provided AudioCue
     *
     * @param sound The AudioCue to switch to
     */
    fun switch(sound: AudioCue?) {
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
            fadeOutTweens.addNew(oldSound, transitionTime, fun(frac: Double) {
                oldSound.setVolume(0, targetVolume * (1 - frac))
            }).play()
        }

        /*
        Fade in the new
         */
        currentSound = sound
        onSoundChange(sound)

        if (sound != null) {
            fadeInTween = object : Transition() {
                init {
                    cycleDuration = Duration.seconds(transitionTime)
                }

                override fun interpolate(frac: Double) {
                    sound.setVolume(0, targetVolume * frac)
                }
            }
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
     * Fires when the primary AudioCue instance has changed.
     *
     * @param newSound The new primary AudioCue instance
     */
    fun onSoundChange(newSound: AudioCue?) {}
}