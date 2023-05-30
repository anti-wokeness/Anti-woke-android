package no.freedom.anti_woke.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import no.freedom.anti_woke.BuildConfig
import no.freedom.anti_woke.R


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val versionText: TextView = root.findViewById(R.id.VersionText)
        versionText.text = "Version: " + BuildConfig.VERSION_NAME

        val otherAvailabilityText: TextView = root.findViewById(R.id.otherAvailabilityText)
        val otherAvailabilityTextString = resources.getString(R.string.other_availability_text)
        val sourceText = "<a href='https://chrome.google.com/webstore/detail/anti-woke/lmilkgfpcjmmcbnenlpjknhleffaeoci'>Browser extension</a> and on " + "<a href='https://anti-woke.net/'>anti-woke.net</a>."
        otherAvailabilityText.text = HtmlCompat.fromHtml("$otherAvailabilityTextString $sourceText", HtmlCompat.FROM_HTML_MODE_LEGACY)
        otherAvailabilityText.movementMethod = LinkMovementMethod.getInstance()

        val supportText: TextView = root.findViewById(R.id.supportText)
        val supportTextString = resources.getString(R.string.support_project)
        val support = "<a href='https://www.buymeacoffee.com/antiwokeness'>Buy me a Coffee</a>"
        supportText.text = HtmlCompat.fromHtml("$supportTextString $support", HtmlCompat.FROM_HTML_MODE_LEGACY)
        supportText.movementMethod = LinkMovementMethod.getInstance()

        return root
    }
}
