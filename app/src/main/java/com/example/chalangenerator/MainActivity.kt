package com.example.chalangenerator

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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
    lateinit var selectOffence:TextView
    lateinit var offensesImage:ImageView
   lateinit var selectedOffenses: BooleanArray
   lateinit var offensesList: ArrayList<Int>
   lateinit var totalAmount :IntArray

   lateinit var challanAmount  : TextView

   val offensesArray = arrayOf("1. B43-200-Driver of motor cycle without safety helmet.",
       "2. B41-500-Obstructing traffic Remarks: Noted by spotter",
       "3. B28-1000 - Driving at night without proper lights."
   )

    @SuppressLint("SimpleDateFormat", "NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        challanAmount = findViewById(R.id.challanAmount)

        btn = findViewById(R.id.generatePDF)
        linearLayout = findViewById(R.id.pdf)
        currentTime = findViewById(R.id.currentTime)
        timee = findViewById(R.id.time)
        datee = findViewById(R.id.date)
        collectionId = findViewById(R.id.collectionId)
        spinner = findViewById(R.id.spinner)
        spinner2 = findViewById(R.id.spinner2)
        selectOffence = findViewById(R.id.offense)
        offensesImage = findViewById(R.id.offenses)

        selectedOffenses = BooleanArray(offensesArray.size)

        selectOffence.visibility = View.GONE

        offensesImage.setOnClickListener {
            showOffences()
        }
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
        timee.text = currentTimeFormate
        datee.text = currentDateFormate


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

                offensesImage.visibility = View.VISIBLE
                selectOffence.visibility = View.GONE
                challanAmount.text="0"
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
    @SuppressLint("SetTextI18n")
    private fun showOffences(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Offense(s)")
        val stringBuilder = StringBuilder()
        totalAmount = IntArray(3)
        offensesList = ArrayList<Int>()
        builder.setMultiChoiceItems(offensesArray, selectedOffenses,
            DialogInterface.OnMultiChoiceClickListener { dialogInterface, i, b ->
                if (b){
                offensesList.add(i)

                if(i==0){
                    totalAmount[0] = 200
                }
                if(i==1){
                    totalAmount[1] = 500
                }
                if(i==2){
                    totalAmount[2] =1000
                }


            }
                else{
                    offensesList.remove(i)
            }
            }).setPositiveButton("ok",DialogInterface.OnClickListener { dialogInterface, i ->

            for(j in 0 until offensesList.size){
                stringBuilder.append(offensesArray[offensesList.get(j)])
                if(j!= offensesList.size){
                    stringBuilder.append("\n")
                }

                    selectedOffenses[0] = false
                    selectedOffenses[1] = false
                    selectedOffenses[2] = false

            }
            selectOffence.text = "$stringBuilder"
            selectOffence.visibility = View.VISIBLE
            offensesImage.visibility = View.GONE
            var total = totalAmount[0]+totalAmount[1]+totalAmount[2]
            challanAmount.text = "$total"

            stringBuilder.setLength(0)

        }
            ).setNegativeButton("cancel",DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        ).setNeutralButton("clear all",DialogInterface.OnClickListener { dialogInterface, i ->
            for(j in selectedOffenses.indices){
                selectedOffenses[j] = false
                selectOffence.text = ""
                offensesImage.visibility = View.VISIBLE
            }
        }
        )
        builder.show()
    }

}