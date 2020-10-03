package com.mikhailgrigorev.game.loader

import java.io.File

class FileReader {
    init {
        val name: String = FileReader::class.java.name.replace(".", File.separator)
        val path =
            System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "test.txt"
        val f: File = File(path)
        f.forEachLine {
            val data = it.split(" ")
            val x = data[0]
            val size = data[1]
            val y = data[2]
            val id = data[3]
            val name = data[4]
            val desc = data[5]
            val bitmapId = data[6]
            val group = data[7]
        }
    }
}