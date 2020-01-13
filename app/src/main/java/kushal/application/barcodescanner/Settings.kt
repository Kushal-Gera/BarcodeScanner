package kushal.application.barcodescanner

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kushal.application.custombar.CustomBar


val SHARED_PREF = "shared_pref"
val START = "start"
val OPEN_LINK = "open_link"

class Settings : AppCompatActivity() {


    val WEB_APP_LINK = "http://play.google.com/store/apps/details?id=" + "kushal.application.gym"
    val GMAIL_LINK = "kushalgera1212@gmail.com"

    val sharedPreferences by lazy {
        applicationContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        var IS_START = sharedPreferences.getBoolean(START, false)
        setting_switch.isChecked = IS_START

        var IS_OPEN_LINK = sharedPreferences.getBoolean(OPEN_LINK, false)
        open_link.isChecked = IS_OPEN_LINK


        back.setOnClickListener {
            onBackPressed()
        }
        setting_dev.setOnClickListener {
            CustomBar(it, "Developed by Kushal Gera :", CustomBar.LENGTH_LONG).run {

                setTextSize(16f)

                actionTextColor(R.color.colorAccent)
                actionText("Git Hub", View.OnClickListener {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Kushal-Gera"))
                    )
                }, false, 16f)

                setBackground(R.drawable.bg_toolbar)

                setMargins(15, 0, 15, 30)

                show()

            }
        }
        setting_share.setOnClickListener { shareIT() }
        setting_rate.setOnClickListener { rateUs() }
        setting_suggestions.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("mailto:$GMAIL_LINK")
            i.putExtra(
                Intent.EXTRA_SUBJECT,
                "Suggestion for the App '${resources.getString(R.string.app_name)}'"
            )
            i.putExtra(Intent.EXTRA_TEXT, "I have a suggestion that: ")
            startActivity(i)
        }

        setting_switch.setOnClickListener {
            IS_START = !IS_START
            setting_switch.isChecked = IS_START
            sharedPreferences.edit().putBoolean(START, IS_START).apply()
        }
        open_link.setOnClickListener {
            IS_OPEN_LINK = !IS_OPEN_LINK
            open_link.isChecked = IS_OPEN_LINK
            sharedPreferences.edit().putBoolean(OPEN_LINK, IS_OPEN_LINK).apply()
        }

    }

    private fun shareIT() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "${resources.getString(R.string.app_name)}\n\n$WEB_APP_LINK")
        startActivity(Intent.createChooser(i, "Share Via"))
    }

    private fun rateUs() { //        open playStore if present otherwise go to chrome
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEB_APP_LINK)))
        }
    }

}
