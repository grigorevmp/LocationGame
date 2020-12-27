
package com.mikhailgrigorev.game.loader

import android.content.Context
import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

class CSVReader(context: Context, fileName: String) {
    /**
     * Read data from csv file line by line and load them to @data
     */


    private val mContext: Context = context
    private var am: AssetManager = mContext.assets

    private var ins: InputStream = am.open(fileName)

    private val reader = BufferedReader(InputStreamReader(ins))
    private var line = reader.readLine()

    var data = ArrayList<List<String>>()
        private set

    init{
        while(line != null){
            val lineArr = line.split(",")
            data.add(lineArr)
            line = reader.readLine()
        }
    }

}