package no.freedom.anti_woke.ui.woke

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import no.freedom.anti_woke.ui.SharedPreferencesStorage


class WokeFragment : ParentFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.apiUrl = "https://api.npoint.io/f998e9a09ba383f73981"
        super.storageSaveString = SharedPreferencesStorage.woke
        super.storageSynchString = SharedPreferencesStorage.lastSynchWoke
        super.fragment = this
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
