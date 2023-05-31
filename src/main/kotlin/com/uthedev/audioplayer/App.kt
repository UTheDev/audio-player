package com.uthedev.audioplayer

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage

class App : Application() {
    private var audioPlayer: AudioPlayer? = null

    override fun start(stage: Stage) {
        // Prompt for a list of WAV files
        val fileList = WavChooser(stage).prompt()

        if ((fileList != null) && (fileList.size > 0)) {
            println("Received ${fileList.size} files from input. Now loading...")
            audioPlayer = AudioPlayer()

            for (file in fileList) {
                audioPlayer!!.add(file.toURI().toURL())
            }

            val songList = SongList()
            songList.update(audioPlayer!!)

            //val fxmlLoader = FXMLLoader(App::class.java.getResource("hello-view.fxml"))
            val scene = Scene(Group(songList))//Scene(fxmlLoader.load(), 320.0, 240.0)
            //scene.fill = Color.BLACK
            stage.title = "Audio Player Demo"
            stage.scene = scene

            stage.show()
        } else {
            // Close immediately, as we've received no valid files from input
            stage.close()
            Platform.exit()
        }
    }

    override fun stop() {
        if (audioPlayer != null) {
            audioPlayer!!.clear()
        }
    }
}

fun main() {
    Application.launch(App::class.java)
}