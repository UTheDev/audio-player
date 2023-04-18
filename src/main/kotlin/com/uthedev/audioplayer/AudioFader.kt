package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue

import java.util.HashMap

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
 * @param old The AudioCue to transition away from
 * @param new The AudioCue to transition to
 */
class AudioFader(old: AudioCue, new: AudioCue, targetVolume: Double) {
    /**
     * The list of tweens (transitions) that are currently reducing the volume of corresponding AudioCues to 0.
     */
    private val fadeOutTweens = HashMap<AudioCue, Transition>()

    /**
     * The tween that's fading the current sound in
     */
    private lateinit var fadeInTween: Transition

    /**
     * The current AudioCue
     */
    private lateinit var currentSound: AudioCue

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

    fun isReversing(): Boolean {
        return transition.rate == -1.0
    }

    fun pause() {
        transition.pause()
    }

    fun play() {
        pause()
        transition.rate = 1.0
        transition.play()
    }

    fun reverse() {
        pause()
        transition.rate = -1.0
        transition.play()
    }

    /**
     * Called when the transition has been fully reversed after already being played
     */
    fun onFullReverse() {}

    /**
     * Called when the transition has finished
     */
    fun onFinish() {}
}