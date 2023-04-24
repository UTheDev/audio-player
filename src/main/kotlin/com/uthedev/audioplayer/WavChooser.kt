package com.uthedev.audioplayer

import java.io.File

import javafx.stage.FileChooser
import javafx.stage.Window

/**
 * Prompts the user to select one or more WAV files
 */
class WavChooser(parentWindow: Window) {
    private val fileChooser = FileChooser()
    private val window = parentWindow

    fun prompt(): MutableList<File>? {
        return fileChooser.showOpenMultipleDialog(window)
    }

    init {
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("WAV Files", "*.wav"))
    }
}