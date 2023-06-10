package no.freedom.anti_woke.ui

import com.google.gson.Gson

class Item(
    text: String,
    sources: String) {

    var name: String = ""
    var text: String? = ""
    var sources: String? = ""
    var visitUrl: String? = ""
    var linkUrl: String? = ""
    var linkText: String? = ""
    var childText: String? = ""
    var duplicate: Boolean = false
    var children: List<Item>? = listOf()
    var otherNames: List<String>? = listOf()

    init {
        this.text = text
        this.sources = sources
        this.children = children ?: listOf()
        this.otherNames = otherNames ?: listOf()
    }

    fun clone(): Item
    {
        val stringItem = Gson().toJson(this, Item::class.java)
        return Gson().fromJson(stringItem, Item::class.java)
    }
}