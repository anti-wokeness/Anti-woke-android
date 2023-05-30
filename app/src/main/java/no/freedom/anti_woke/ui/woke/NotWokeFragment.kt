package no.freedom.anti_woke.ui.woke

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import no.freedom.anti_woke.ui.SharedPreferencesStorage


class NotWokeFragment : ParentFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.apiUrl = "https://api.npoint.io/c680525c1d05a9a8ebff"
        super.storageSaveString = SharedPreferencesStorage.notWoke
        super.storageSynchString = SharedPreferencesStorage.lastSynchNotWoke
        super.fragment = this
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
