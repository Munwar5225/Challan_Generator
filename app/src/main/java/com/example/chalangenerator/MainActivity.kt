package com.example.chalangenerator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var btn: Button
    lateinit var linearLayout: LinearLayout
    lateinit var bitmap: Bitmap
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private lateinit var currentTime:TextView
    private lateinit var timee:TextView
    private lateinit var datee:TextView
    private val sharedPrefFile = "challanGenerator"
    private lateinit var collectionId:TextView
    lateinit var sharedPreferences:SharedPreferences
    lateinit var array:Array<Int>
    lateinit var vehicleTypeArray:Array<String>
    private lateinit var spinner: Spinner
    lateinit var spinner2: Spinner

    @SuppressLint("SimpleDateFormat", "NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.generatePDF)
        linearLayout = findViewById(R.id.pdf)
        currentTime = findViewById(R.id.currentTime)
        timee = findViewById(R.id.time)
        datee = findViewById(R.id.date)
        collectionId = findViewById(R.id.collectionId)
        spinner = findViewById(R.id.spinner)
        spinner2 = findViewById(R.id.spinner2)



        array = arrayOf(1,2,3,4)
        val adapters:ArrayAdapter<Int>  = ArrayAdapter(this,android.R.layout.simple_spinner_item,array)
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapters

        vehicleTypeArray = arrayOf("HTV","LTV")
        val adapters2:ArrayAdapter<String>  = ArrayAdapter(this,android.R.layout.simple_spinner_item,vehicleTypeArray)
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapters2


        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern ("HH:mm:ss")

        val current = now.format(formatter)
        val currentDateFormate = now.format(dateFormatter)
        val currentTimeFormate = now.format(timeFormatter)

        currentTime.text = current
        timee.text = currentDateFormate
        datee.text = currentTimeFormate


        sharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        collectionId.text = sharedPreferences.getInt("collectionId",1).toString()

        var id:Int = Integer.parseInt(collectionId.text.toString())

        btn.setOnClickListener {

            editor.putInt("collectionId",id)
            editor.apply()
            editor.commit()
            editor.putInt("collectionId",id)
            editor.apply()
            editor.commit()
            collectionId.text = sharedPreferences.getInt("collectionId",0).toString()
            id++
            Log.d("size", ""+linearLayout.width+" " + linearLayout.height)
            bitmap = LoadBitmap(linearLayout, linearLayout.width, linearLayout.height)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                createPDF()
            }
            else{
                Toast.makeText(this, "Not supported mobile", Toast.LENGTH_LONG).show()
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createPDF() {
        Toast.makeText(this, "Pdf Created", Toast.LENGTH_SHORT).show()
     val windowManag = getSystemService(Context.WINDOW_SERVICE)
        val  displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val convertWidth:Int  = width
        val convertHeight:Int  = height
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHeight,1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true)
        canvas.drawBitmap(bitmap, 0f,0f,null)
        document.finishPage(page)

        val myDir: File = this.filesDir
        // Documents Path
        // Documents Path
        val documents = "documents/data${sharedPreferences.getInt("collectionId",0)}"
        val documentsFolder = File(myDir, documents)
        documentsFolder.mkdirs()

        val targetPdf = "/storage/emulated/0/documents/data${sharedPreferences.getInt("collectionId",0)}.pdf"
        Toast.makeText(this, "at $targetPdf", Toast.LENGTH_SHORT).show()

        var file:File = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(file))
        }
        catch (e:IOException){
            e.printStackTrace()
            Toast.makeText(this, "$e",Toast.LENGTH_SHORT).show()
        }
        document.close()
        openPdf()


    }

    private fun openPdf() {
    }

    private fun LoadBitmap(v: View, width: Int, height: Int): Bitmap {
        bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }

}