package com.uthedev.audioplayer

import javafx.scene.layout.GridPane
import javafx.scene.control.Button

import com.adonax.audiocue.AudioCue
import java.net.URL

/* resource that might be worth considering:
https://openjfx.io/javadoc/20/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html#overview
*/
class SongList(): GridPane() {
    var numColumns = 2

    fun update(player: AudioPlayer) {
        children.clear()

        var row: Int = 0
        var col: Int = 0
        for ((i, _) in player.getMap()) {
            val button = Button(i.path)
            button.setOnMouseClicked {
                println("switching to ${i.path}")
                player.play(i)
            }

            add(button, col, row)
            println("added button $row")

            col++

            // remember that column and row indexes start at 0
            if (col >= numColumns) {
                col = 0
                row++
            }
        }
    }
}