package com.example.morsecode

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

fun dot(): String = "."

fun dash(): String = "-"

fun newln(): String ="\n"

fun letterspace(): String = "  "

fun wordspace(): String = "    "

fun backspace(x:String): String{
    var a: String = x
    var b: Int = a.length
    var c: String = ""
    var d: Int = b-2

    for(i in 0..d){
        c+=a[i]
    }

    return c.trimEnd().trimStart()
}

fun <K, V> getKey(map: Map<K, V>, target: V): K {
    return map.keys.first { target == map[it] };
}

fun convertor(x:String):String{
    var x = x.toUpperCase()
    var morse_list = mapOf(
        "" to "",
        ".-" to "A",
        "-.."  to "B",
        ".-.-" to "C",
        "-..." to "D",
        "." to "E",
        "..-." to "F",
        "--." to "G",
        "...." to "H",
        ".." to "I",
        ".---" to "J",
        "-.-" to "K",
        ".-.." to "L",
        "--" to "M",
        "-." to "N",
        "---" to "O",
        ".--." to "P",
        "--.-" to "Q",
        ".-." to "R",
        "..." to "S",
        "-" to "T",
        "..-" to "U",
        "...-" to "V",
        ".--" to "W",
        "-..-" to "X",
        "-.--" to "Y",
        "--.." to "Z"
    )
    var alternate:String = ""
    //keys - morse
    //vals - alpha
    try {
        alternate = (if(morse_list[x]==null){getKey(morse_list,x)} else{morse_list[x]}).toString()
    }catch (e:NoSuchElementException){
        alternate ="  "
    }

    return alternate

}

fun m2ana2m(y:String):String{
    var x=y.replace("\n","")
    var letters = mutableListOf<List<String>>()
    var con = mutableListOf<String>()
    var sent: String = ""

    if ("." in x || "-" in x){
        var words = x.split("    ")//".-  .-  .-    .-  .-  .- "->"[".-  .-  .-",".-  .-  .-"]
        for(word in words){
            var let = word.split("  ")//[[".-",".-",".-"],[".-",".-",".-"]]
            letters.add(let)
        }
        for(i in letters){
            var temp:String = ""
            for(j in i){
                temp += convertor(j)
            }
            temp = temp.trimEnd().trimStart()
            con.add(temp)
        }
        sent = con.joinToString(separator =" ")
        sent = sent.chunked(50).joinToString(" \n")

    }else{
        var words = x.split(" ")
        for(word in words){
            var let = word.split("")
            letters.add(let)
        }
        for(i in letters){
            var temp:String = ""
            for(j in i){
                temp+="  "
                temp+=convertor(j)

            }
            temp = temp.trimEnd().trimStart()
            con.add(temp)
        }
        sent= con.joinToString(separator ="    ")
        sent = sent.chunked(50).joinToString(" \n")
    }
    return sent

}



class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    var camman: CameraManager? = null
    var camid:String? = null
    var state: Boolean? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        fun noflasherror(){
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("No Flash")
            builder.setMessage("Your device does not support flash capabilities")
            flash.isClickable = false
        }

        morsecode.showSoftInputOnFocus = false
        //morsecode.inputType = InputType.TYPE_NULL
        alphabets.filters = alphabets.filters + InputFilter.AllCaps()

        //Dot - dot_button
        //Dash - dash_button
        //lspace - letter_space
        //done - done_button
        //bspace - backspace
        //wspace - word_space
        //words - alphabets(edittext)
        //morse - morsecode(edittext)

        dot_button.setOnClickListener {
            morsecode.requestFocus()
            morsecode.append(dot())
            alphabets.setText(m2ana2m(morsecode.text.toString()))
            morsecode.setSelection(morsecode.text.length)

        }

        dash_button.setOnClickListener {
            morsecode.requestFocus()
            morsecode.append(dash())
            alphabets.setText(m2ana2m(morsecode.text.toString()))
            morsecode.setSelection(morsecode.text.length)

        }

        word_space.setOnClickListener {
            morsecode.requestFocus()
            morsecode.append(wordspace())
            alphabets.setText(m2ana2m(morsecode.text.toString()))
            morsecode.setSelection(morsecode.text.length)

        }
        letter_space.setOnClickListener {
            morsecode.requestFocus()
            morsecode.append(letterspace())
            alphabets.setText(m2ana2m(morsecode.text.toString()))
            morsecode.setSelection(morsecode.text.length)

        }

        backspace.setOnClickListener {
            morsecode.requestFocus()
            morsecode.setText(backspace(morsecode.text.toString()))
            alphabets.setText(m2ana2m(morsecode.text.toString()))
            morsecode.setSelection(morsecode.text.length)

        }
        alphabets.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (alphabets.hasFocus()) {
                    morsecode.setText(m2ana2m(alphabets.text.toString()))

                }
            }
            })

        clear_button.setOnClickListener {
            morsecode.setText("")
            alphabets.setText("")
            morsecode.clearFocus()
            alphabets.clearFocus()
        }

        fun flash(x: String){
        camman=getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camid = camman!!.cameraIdList[0]
            if(x=="."){
                camman!!.setTorchMode(camman!!.cameraIdList[0],true)
                Thread.sleep(200)
                camman!!.setTorchMode(camman!!.cameraIdList[0],false)
                Thread.sleep(900)
            }else if(x=="-") {
                camman!!.setTorchMode(camman!!.cameraIdList[0], true)
                Thread.sleep(740)
                camman!!.setTorchMode(camman!!.cameraIdList[0],false)
                Thread.sleep(900)
            }else{
                camman!!.setTorchMode(camman!!.cameraIdList[0],false)
                Thread.sleep(1100)
            }

        }
        flash.setOnClickListener {

            var isFlashAvailable: Boolean = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            if(isFlashAvailable==false){
                noflasherror()
            }else{
                var morse:String = morsecode.text.toString()
                for(i in morse){
                    flash(i.toString())
                }
            }
        }

        fun beep_player(){
            var list = mutableListOf<String>()
            var text= morsecode.text.toString()

            for (i in text){
                list.add(i.toString())
                if (i=='.'){
                    mediaPlayer = MediaPlayer.create(applicationContext,R.raw.dot2)
                    mediaPlayer.start()
                    Thread.sleep(25)
                }
                if(i=='-'){
                    mediaPlayer = MediaPlayer.create(applicationContext,R.raw.dash2)
                    mediaPlayer.start()
                    Thread.sleep(25)
                }
                Thread.sleep(250)
            }
        }
        sound.setOnClickListener {
            val t = Thread()

            thread(start = true,isDaemon = true)  {
              beep_player()
            }

        }

        fun vibrate(x:Int){
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(x.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(200)
            }
        }

        vibrate.setOnClickListener {
            var code:String = morsecode.text.toString()
            for (i in code) {
                if (i=='.') {
                    vibrate(100)
                    Thread.sleep(300)
                }else if(i=='-'){
                    vibrate(280)
                    Thread.sleep(400)
                }else if(i==' '){
                    Thread.sleep(450)
                }
            }
        }
    }
}
