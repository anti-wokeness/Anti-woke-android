package no.freedom.anti_woke.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.coroutines.*
import no.freedom.anti_woke.R
import no.freedom.anti_woke.ui.woke.ParentFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext


class JsonTask(context: Fragment, storage: String, synch: String) : CoroutineScope {

    private lateinit var dialog: AlertDialog
    private val storageSaveString: String = storage
    private val storageSynchString: String = synch
    private val activityReference: WeakReference<Fragment> = WeakReference(context)

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun execute(urlString: String) = launch {
        onPreExecute()
        val result = doInBackground(urlString)
        onPostExecute(result)
    }

    private fun onPreExecute() {
        try {
            dialog = setProgressDialog(activityReference.get()!!.requireContext())
            dialog.show()
        }catch(e: Exception){}
    }

    private suspend fun doInBackground(urlString: String): JsonData? = withContext(Dispatchers.IO) {
        try {
            val url = readUrl(urlString)
            return@withContext Gson().fromJson(url, JsonData::class.java)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext null
    }

    private fun readUrl(urlString: String): String? {
        var reader: BufferedReader? = null
        return try {
            val url = URL(urlString)
            reader = BufferedReader(InputStreamReader(url.openStream()))
            val buffer = StringBuffer()
            var read: Int
            val chars = CharArray(1024)
            while (reader.read(chars).also { read = it } != -1) buffer.append(chars, 0, read)
            buffer.toString()
        } finally {
            try {
                reader?.close()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun onPostExecute(result: JsonData?) {
        if (dialog.isShowing)
            dialog.dismiss()

        try{
            val activity = activityReference.get() as ParentFragment

            if(result == null) {
                val jsonData = activity.storage.getJsonData(storageSaveString)
                if(jsonData != null)
                    activity.updateViewData(jsonData)
                return
            }

            val jsonData = flatten(result)
            activity.storage.setJsonData(storageSaveString, jsonData)
            activity.storage.set(storageSynchString, activity.getTodayFormatted())
            activity.updateViewData(jsonData)
        }
        catch (e: Exception){}
    }

    private fun flatten(urlsToWarn: JsonData) : JsonData {
        val flattened = mutableListOf<Item>()
        val wokeText = urlsToWarn.woke
        val notWokeText = urlsToWarn.notWoke
        val wokeRacistText = urlsToWarn.wokeRacist
        val companies = urlsToWarn.companies

        for(item in companies.orEmpty())
        {
            if(item.text.isNullOrEmpty()) {
                when {
                    wokeText.isNotEmpty() -> item.text = item.name + wokeText
                    notWokeText.isNotEmpty() -> item.text = item.name + notWokeText
                    else -> item.text = ""
                }
            }
            else if(item.text == "{wokeRacist}")
                item.text = wokeRacistText

            flattened.add(item)

            for(otherName in item.otherNames.orEmpty())
            {
                val itemCopy = item.clone()
                itemCopy.name = otherName
                itemCopy.duplicate = true
                if (item.name.toLowerCase(Locale.ROOT) != itemCopy.name.toLowerCase(Locale.ROOT))
                    flattened.add(itemCopy)
            }

            for(itemChild in item.children.orEmpty())
            {
                if(itemChild.sources.isNullOrEmpty())
                    itemChild.sources = item.sources

                if(itemChild.text.isNullOrEmpty())
                {
                    var childText = item.childText

                    if(childText.isNullOrEmpty())
                        childText = itemChild.name + wokeText
                    if(childText.contains("{1}"))
                        childText = childText.replace("{1}", itemChild.name)
                    if(childText.contains("{2}"))
                        childText = childText.replace("{2}", item.text!!)
                    itemChild.text = childText
                }

                for(otherName in itemChild.otherNames.orEmpty())
                {
                    val itemCopy = item.clone()
                    itemCopy.name = otherName
                    itemCopy.duplicate = true
                    if(item.name.toLowerCase(Locale.ROOT) != itemCopy.name.toLowerCase(Locale.ROOT))
                        flattened.add(itemCopy)
                }

                if(item.name.toLowerCase(Locale.ROOT) != itemChild.name.toLowerCase(Locale.ROOT))
                {
                    flattened.add(itemChild)
                }
            }
            item.children = listOf()
        }
        flattened.sortBy { x -> x.name.toLowerCase(Locale.ROOT) }
        urlsToWarn.companies = flattened
        return urlsToWarn
    }

    private fun setProgressDialog(context: Context): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = context.getString(R.string.updating_list)
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }
}