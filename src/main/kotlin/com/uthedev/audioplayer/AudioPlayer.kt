package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue
import java.net.URL

/**
 * This class is responsible for playing a bunch of AudioCues.
 *
 * Currently, playback is based on filenames
 */
class AudioPlayer {
    private val map = HashMap<URL, Sound>()
    private val fader = SoundFader(1.0)

    fun getMap(): HashMap<URL, Sound> {
        return map
    }

    fun clear() {
        for ((_, v) in map) {
            v!!.audioCue.close()
        }

        map.clear()
    }

    fun remove(path: URL) {
        val sound = map.remove(path)
        sound!!.audioCue.close()
    }

    fun add(path: URL) {
        if (!path.path.endsWith(".wav")) {
            throw IllegalArgumentException("At the moment, AudioPlayer can only play .wav files")
        }

        val audio = AudioCue.makeStereoCue(path, 1)
        audio.open()
        map[path] = Sound(audio)
    }

    fun stop() {
        fader.switch(null)
    }

    fun play(path: URL) {
        val sound: Sound? = map[path]

        if (sound != null) {
            val audio = sound.audioCue
            val instId = sound.instanceId
            /*
            * Reset cursor position if not currently playing
            */
            if (!audio.getIsPlaying(instId)) {
                audio.setFramePosition(instId, 0.0)
                audio.setVolume(instId, 0.0)

                audio.start(instId)
            }

            fader.switch(sound)
        }
    }
}