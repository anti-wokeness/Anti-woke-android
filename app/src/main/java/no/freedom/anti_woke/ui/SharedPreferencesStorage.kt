package no.freedom.anti_woke.ui

import android.content.Context
import com.google.gson.Gson

class SharedPreferencesStorage(ctx: Context) {
    companion object {
        const val woke: String = "woke"
        const val lastSynchWoke: String = "lastSynchWoke"

        const val notWoke: String = "notWoke"
        const val lastSynchNotWoke: String = "lastSynchNotWoke"
    }

    private var context: Context = ctx
    private var gson: Gson = Gson()
    private val pref = context.getSharedPreferences(this::class.java.simpleName, Context.MODE_PRIVATE)

    fun set(key: String, value: String) = pref.edit().putString(key, value).apply()
    fun get(key: String, defaultValue: String = ""): String = pref.getString(key, defaultValue) ?: ""

    fun setJsonData(key: String, list: JsonData) {
        val editor = pref.edit()
        editor.putString(key, gson.toJson(list))
        editor.apply()
    }

    fun getJsonData(key: String): JsonData? {
        val data = pref.getString(key, null) ?: return null
        return gson.fromJson(data, JsonData::class.java)
    }
}