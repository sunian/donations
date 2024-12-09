import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream

class AggregateReportTsvFactory(
    private val year: Int,
    filename: String,
) : DocumentFactory {
    private val outputPath = "${Defaults.outputDir}/$year/$filename.tsv"
    private val fileWriter: BufferedWriter

    init {
        File(outputPath).parentFile.mkdirs()
        fileWriter = FileOutputStream(outputPath).bufferedWriter()
        fileWriter.write("Name")
        val months = "JFMAMJJASOND"
        repeat(12) {
            fileWriter.write("\t${it + 1} ${months[it]}")
        }
    }

    override fun addFooter() {}
    override fun addPageBreak() {}

    override fun addReport(donations: List<Donation>) {
        if (donations.isEmpty()) {
            return
        }
        fileWriter.newLine()
        fileWriter.write(donations.first().name)
        repeat(12) { month ->
            val donationCount = donations.count { it.month == month + 1 }
            val content = when {
                donationCount > 0 -> "✅"
                else -> ""
            }
            fileWriter.write("\t$content")
        }
    }

    override fun writeToFile(): String {
        fileWriter.flush()
        fileWriter.close()
        println("tsv written successfully")
        return outputPath
    }
}