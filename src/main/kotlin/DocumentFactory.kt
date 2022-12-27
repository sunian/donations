interface DocumentFactory {
    fun addFooter()
    fun addReport(donations: List<Donation>)
    fun addPageBreak()
    fun writeToFile(): String
}