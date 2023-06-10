package no.freedom.anti_woke.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.solver.widgets.analyzer.VerticalWidgetRun
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.fragment_woke.*
import no.freedom.anti_woke.R
import no.freedom.anti_woke.ui.woke.ParentFragment
import java.util.*
import kotlin.math.min


class CustomExpandableListAdapter
internal constructor(
    private val context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<Item>>,
    private val parentListView: ParentFragment
) : BaseExpandableListAdapter(), Filterable {
    private var titleListFiltered: List<String>

    init {
        titleListFiltered = titleList
        filter.filter(parentListView.listSearch.query)
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.dataList[titleListFiltered[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    private fun getSourceText(sourceItem: String): String{
        var sourceText = ""
        if(sourceItem != "")
        {
            val sources = sourceItem.split(',')
            sourceText += "("
            for(i in sources.indices)
            {
                sourceText += "<a href='" + sources[i] + "'>source</a>"

                if(i + 1 < sources.size)
                    sourceText += ", "
            }
            sourceText += ")"
        }
        return sourceText
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var localConvertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as Item
        if (localConvertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            localConvertView = layoutInflater.inflate(R.layout.list_item, null)
        }

        val expandedListTextView = localConvertView!!.findViewById<TextView>(R.id.listItemText)
        var text = expandedListText.text ?: ""
        if(!expandedListText.sources.isNullOrEmpty())
            text += " " + getSourceText(expandedListText.sources!!)

        expandedListTextView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        expandedListTextView.movementMethod = LinkMovementMethod.getInstance()

        val alternativesView = localConvertView.findViewById<TextView>(R.id.listItemAlternative)
        if(!expandedListText.linkText.isNullOrEmpty())
        {
            alternativesView.visibility = View.VISIBLE
            alternativesView.text = HtmlCompat.fromHtml("An alternative to " + expandedListText.name + " is <a href='" + expandedListText.linkUrl + "'>" + expandedListText.linkText + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            alternativesView.movementMethod = LinkMovementMethod.getInstance()
        }
        else
            alternativesView.visibility = View.GONE

        val visitView = localConvertView.findViewById<TextView>(R.id.listItemVisitUrl)
        if(!expandedListText.visitUrl.isNullOrEmpty())
        {
            visitView.visibility = View.VISIBLE
            visitView.text = HtmlCompat.fromHtml("Visit <a href='" + expandedListText.visitUrl + "'>" + expandedListText.name + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            visitView.movementMethod = LinkMovementMethod.getInstance()
        }
        else
            visitView.visibility = View.GONE

        return localConvertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[titleListFiltered[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleListFiltered[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleListFiltered.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var localConvertView = convertView
        if (localConvertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            localConvertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        try{
            val listTitle = getGroup(listPosition) as String
            val listTitleTextView = localConvertView!!.findViewById<TextView>(R.id.listGroupName)
            listTitleTextView.setTypeface(null, Typeface.BOLD)
            listTitleTextView.text = listTitle

            listTitleTextView.setOnTouchListener { _, _ ->
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(localConvertView.windowToken, 0)
                false
            }
        }catch(e: Exception){}

        return localConvertView!!
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return false
    }

    private fun similarity(s1: String, s2: String) : Float{
        var longer = s1.toLowerCase(Locale.ROOT)
        var shorter = s2.toLowerCase(Locale.ROOT)
        if (s1.length < s2.length) {
            longer = s2
            shorter = s1
        }
        val longerLength = longer.length
        if (longerLength == 0) {
            return 1.0f
        }
        val base = longerLength - levenshtein(longer, shorter)
        return (base + 0f) / longerLength + 0f
    }

    private fun levenshtein(lhs : CharSequence, rhs : CharSequence) : Int {
        if(lhs == rhs) { return 0 }
        if(lhs.isEmpty()) { return rhs.length }
        if(rhs.isEmpty()) { return lhs.length }

        val lhsLength = lhs.length + 1
        val rhsLength = rhs.length + 1

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1 until rhsLength) {
            newCost[0] = i

            for (j in 1 until lhsLength) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isEmpty()) {
                    filterResults.count = titleList.size
                    filterResults.values = titleList
                } else {
                    titleListFiltered = listOf()
                    val resultsModel: MutableList<String> = ArrayList()
                    val searchStr = constraint.toString().toLowerCase(Locale.ROOT)

                    for (itemName in titleList)
                    {
                        if (shouldDisplay(itemName, searchStr)){
                            resultsModel.add(itemName)
                        }
                        else{
                            val item = dataList[itemName]?.first()
                            for (name in item?.otherNames.orEmpty())
                            {
                                if (shouldDisplay(name, searchStr))
                                {
                                    resultsModel.add(itemName)
                                    break
                                }
                            }
                        }
                    }

                    filterResults.count = resultsModel.size
                    filterResults.values = resultsModel
                }

                parentListView.updateResultsNumber(filterResults.count)
                return filterResults
            }

            private fun shouldDisplay(itemName: String, searchStr: String): Boolean{
                val sim = similarity(itemName, searchStr)
                if ((sim > 0.5) || itemName.toLowerCase(Locale.ROOT).contains(searchStr))
                    return true
                return false
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                val values = results.values ?: listOf<String>()
                try{
                    titleListFiltered = values as List<String>
                    notifyDataSetChanged()

                    if (parentListView.isVisible) {
                        for (g in 0 until parentListView.adapter.groupCount)
                            parentListView.listView.collapseGroup(g)
                    }
                }catch(e: Exception){}
            }
        }
    }
}
