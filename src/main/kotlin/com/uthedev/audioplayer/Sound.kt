package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue

/**
 * Wraps an [com.adonax.audiocue.AudioCue] with some metadata attached to it
 *
 * @param initAudioCue The AudioCue to wrap
 */
class Sound(initAudioCue: AudioCue) {
    val audioCue = initAudioCue

    /**
     * The target volume of the [audioCue]
     */
    var targetVolume = 1

    /**
     * The descriptive name of the sound/music being played by the AudioCue
     */
    var name = ""

    /**
     * The artist that made the sound/music
     */
    var artist = ""
}