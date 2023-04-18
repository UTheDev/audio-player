package com.uthedev.audioplayer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage

class App : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(App::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        scene.fill = Color.BLACK
        stage.title = "Audio Player Demo"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(App::class.java)
}