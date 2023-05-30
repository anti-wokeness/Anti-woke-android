package no.freedom.anti_woke.ui.woke

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_woke.*
import no.freedom.anti_woke.R
import no.freedom.anti_woke.ui.*
import java.text.SimpleDateFormat
import java.util.*


open class ParentFragment : Fragment() {

    private lateinit var wokeViewModel: ParentViewModel
    lateinit var adapter: CustomExpandableListAdapter
    lateinit var listView: ExpandableListView
    lateinit var storage: SharedPreferencesStorage

    lateinit var apiUrl: String
    lateinit var storageSaveString: String
    lateinit var storageSynchString: String
    lateinit var fragment: ParentFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        wokeViewModel = ViewModelProviders.of(this).get(ParentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_woke, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView = expandableListView
        storage = SharedPreferencesStorage(requireContext())

        listSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText.toString())
                return false
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val lastSynched = storage.get(storageSynchString)
        if(isDateInPast(lastSynched))
        {
            JsonTask(this, storageSaveString, storageSynchString).execute(apiUrl)
        }
        else {
            val jsonData = storage.getJsonData(storageSaveString)
            if(jsonData == null)
                JsonTask(this, storageSaveString, storageSynchString).execute(apiUrl)
            else
                updateViewData(jsonData)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateViewData(jsonData: JsonData){
        val listData = HashMap<String, List<Item>>()
        val titleList = ArrayList<String>()

        if(jsonData.companies.isNullOrEmpty())
            jsonData.companies = listOf()

        for(item in jsonData.companies!!)
        {
            val name = item.name
            if(!item.duplicate)
            {
                listData[name] = arrayListOf(item)
                titleList.add(name)
            }
        }
        adapter = CustomExpandableListAdapter(requireContext(), titleList, listData, fragment)
        listView.setAdapter(adapter)

        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val lastSynched = storage.get(storageSynchString)
        var displayString = ""
        val lastSynchSplit = lastSynched.split("/") as MutableList<String>
        if(lastSynchSplit.isNotEmpty())
        {
            lastSynchSplit[1] = months[lastSynchSplit[1].toInt() - 1]
            displayString = lastSynchSplit.joinToString ("-")
        }

        if(displayString == ""){
            val todaySplit = getTodayFormatted().split("/") as MutableList<String>
            todaySplit[1] = months[todaySplit[1].toInt() - 1]
            displayString = todaySplit.joinToString ("-")
        }

        listUpdated.text = "Updated: $displayString"
        updateResultsNumber(titleList.count())
    }

    @SuppressLint("SetTextI18n")
    fun updateResultsNumber(number: Int)
    {
        activity?.runOnUiThread {
            synchDiv.text = "$number searchable results"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTodayFormatted(daysInPast: Int = 0):String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, -daysInPast)
        return sdf.format(calendar.time)
    }

    private fun isDateInPast(dateString: String, daysInPast: Int = 1): Boolean{
        if(dateString.isEmpty())
            return true

        val date = dateString.split('/')
        val today = getTodayFormatted(daysInPast).split('/')

        if(today[2] > date[2])
            return true
        else if(today[1] > date[1] && today[2] >= date[2])
            return true
        else if(today[0] > date[0] && today[1] >= date[1] && today[2] >= date[2])
            return true

        return false
    }
}
