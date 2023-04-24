package com.uthedev.audioplayer

import com.adonax.audiocue.AudioCue
import java.net.URL

/**
 * This class is responsible for playing a bunch of AudioCues.
 *
 * Currently, playback is based on filenames
 */
class AudioPlayer {
    private val map = HashMap<URL, AudioCue>()
    private val fader = AudioFader(1.0)

    fun getMap(): HashMap<URL, AudioCue> {
        return map
    }

    fun clear() {
        for ((_, v) in map) {
            v!!.close()
        }

        map.clear()
    }

    fun remove(path: URL) {
        val audio = map.remove(path)
        audio!!.close()
    }

    fun add(path: URL) {
        if (!path.path.endsWith(".wav")) {
            throw IllegalArgumentException("At the moment, AudioPlayer can only play .wav files")
        }

        val audio = AudioCue.makeStereoCue(path, 1)
        audio.open()
        map[path] = audio
    }

    fun stop() {
        fader.switch(null)
    }

    fun play(path: URL) {
        val audio: AudioCue? = map[path]

        if (audio != null) {
            /*
            * Reset cursor position if not currently playing
            */
            if (!audio.getIsPlaying(0)) {
                audio.setFramePosition(0, 0.0)
                audio.play(0.0)
            }

            fader.switch(audio)
        }
    }
}