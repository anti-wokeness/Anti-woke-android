package no.freedom.anti_woke.ui

class JsonData(
    woke: String,
    notWoke: String,
    wokeRacist: String,
    companies: List<Item>?) {

    var woke: String = ""
    var notWoke: String = ""
    var wokeRacist: String = ""
    var companies: List<Item>? = listOf()

    init {
        this.woke = woke
        this.notWoke = notWoke
        this.wokeRacist = wokeRacist
        this.companies = companies ?: listOf()
    }
}
