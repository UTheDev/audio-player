package com.uthedev.audioplayer

import javafx.scene.layout.GridPane
import javafx.scene.control.Button

import com.adonax.audiocue.AudioCue
import java.net.URL

/* resource that might be worth considering:
https://openjfx.io/javadoc/20/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html#overview
*/
class SongList(): GridPane() {
    fun update(player: AudioPlayer) {
        children.clear()

        for ((i, _) in player.getMap()) {
            val button = Button(i.path)
            button.setOnMouseClicked {
                print("switching to ${i.path}")
                player.play(i)
            }
        }
    }

    init {


    }
}