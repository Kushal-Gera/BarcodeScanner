package kushal.application.barcodescanner

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

var FROM_NOW = "fromNow"

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var scannerView: ZXingScannerView? = null
    var ONHOME = true
    val sharedPreferences by lazy {
        applicationContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)
        adView2.loadAd(adRequest)
        adView3.loadAd(adRequest)
        adView4.loadAd(adRequest)

        scannerView = ZXingScannerView(this)
        launchCam.setOnClickListener {
            ONHOME = false
            doStuff()
        }
        drawer.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/developer?id=Kushal+Gera")
            startActivity(intent)
        }

        settings.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    kushal.application.barcodescanner.Settings::class.java
                )
            )
        }

        if (sharedPreferences.getBoolean(START, false)
            and intent.getBooleanExtra(FROM_NOW, true)
        ) {
            ONHOME = false
            doStuff()
        }


    }

    private fun doStuff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
        }
        setContentView(scannerView)
    }

    override fun handleResult(result: Result?) {

        val myText = result?.text

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(myText)


        if (sharedPreferences.getBoolean(OPEN_LINK, false)) {
            try {
                startActivity(intent)
            } catch (e: Exception) {

                myText?.let { copy(it) }
                Toast.makeText(this, "Copied To Clip Board", Toast.LENGTH_SHORT).show()
            }
        } else {
            android.app.AlertDialog.Builder(
                this,
                R.style.AlertDialogCustom
            )
                .setTitle("Search it on Web ?")
                .setMessage(myText)
                .setPositiveButton("web") { dialogInterface: DialogInterface, i: Int ->
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {

                        myText?.let { copy(it) }
                        Toast.makeText(this, "Copied To Clip Board", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("copy") { dialogInterface: DialogInterface, i: Int ->
                    myText?.let { copy(it) }
                    Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show()

                    dialogInterface.dismiss()
                }
                .setCancelable(false)
                .create().show()
        }

    }

    override fun onResume() {
        super.onResume()

        if (scannerView == null) {
            scannerView = ZXingScannerView(this)
            if (sharedPreferences.getBoolean(START, false))
                doStuff()
        }
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView?.stopCamera()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty()) {
            scannerView?.startCamera()
            scannerView?.setResultHandler(this)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                //denied
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
            } else {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    onResume()
                } else {
                    //set to never ask again
                    showDialog()
                }
            }
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(
            this,
            R.style.AlertDialogCustom
        )
            .setTitle("Permissions are not Granted !")
            .setMessage("Please grant required permissions.")
            .setPositiveButton("Grant Now") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Return") { dialogInterface: DialogInterface, i: Int ->
                finish()
            }
            .setCancelable(false)
            .create().show()
    }

    override fun onBackPressed() {
        if (ONHOME)
            super.onBackPressed()
        else {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FROM_NOW, false)
            startActivity(intent)
            finish()
        }
    }

    fun copy(str: String) {

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text : ", str)

        clipboardManager.setPrimaryClip(clip)
    }

}

