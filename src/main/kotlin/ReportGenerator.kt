import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object ReportGenerator {

    /**
     * maps person name to a list of their donations
     */
    private val donationsByName: MutableMap<String, MutableList<Donation>> = HashMap()

    private var year: Int = 0

    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        print("Please enter the desired year: ")
        year = scanner.nextLine().toInt()
        print("Would you like individual reports or a batch report? (I/B): ")
        val reportType = scanner.nextLine().uppercase()
        print("Would you like to filter people by donation count? (ALL/>#/<#/=#): ")
        val filter = scanner.nextLine().uppercase()
        addDonationsFromCSV("donations - check GFCC.csv", Donation.Type.CHECK)
        addDonationsFromCSV("donations - cash GFCC.csv", Donation.Type.CASH)
        val duplicates = arrayListOf<String>()
        donationsByName.forEach { (name, list) ->
            val set = list.map { "${it.date} ${it.amount}" }.toSet()
            if (list.size != set.size) {
                duplicates.add("duplicates detected for $name")
            }
        }
        val donationsByName = when {
            filter == "ALL" -> donationsByName
            filter.startsWith(">") -> {
                val num = filter.substring(1).toInt()
                donationsByName.filterValues { it.size > num }
            }

            filter.startsWith("<") -> {
                val num = filter.substring(1).toInt()
                donationsByName.filterValues { it.size < num }
            }

            filter.startsWith("=") -> {
                val num = filter.substring(1).toInt()
                donationsByName.filterValues { it.size == num }
            }

            else -> throw IllegalArgumentException("Invalid filter type: $filter")
        }
        print("Would you like DOCX or PDF format? (D/P): ")
        val docFactory: DocumentFactoryProvider = when (val format = scanner.nextLine().uppercase()) {
            "D" -> object : DocumentFactoryProvider {
                override fun provideDocumentFactory(
                    name: String,
                    fein: String,
                    year: Int,
                    filename: String
                ): DocumentFactory = WordDocFactory(name, fein, year, filename)
            }

            "P" -> object : DocumentFactoryProvider {
                override fun provideDocumentFactory(
                    name: String,
                    fein: String,
                    year: Int,
                    filename: String
                ): DocumentFactory = PdfFactory(name, fein, year, filename)
            }

            else -> throw IllegalArgumentException("Invalid format: $format")
        }

        when (reportType) {
            "I" -> donationsByName.forEach { (name, donations) ->
                docFactory.provideDocumentFactory(year = year, filename = "$name (GFCC $year)").run {
                    addFooter()
                    addReport(donations.sorted())
                    writeToFile()
                }
            }

            "B" -> docFactory.provideDocumentFactory(year = year).run {
                addFooter()
                donationsByName.keys.toList().sorted().forEachIndexed { index, name ->
                    addReport(donationsByName[name]!!.sorted())
                    if (index < donationsByName.size - 1) {
                        addPageBreak()
                    }
                }
                writeToFile()
            }

            else -> {
                println("Invalid response. Please try again.")
                return
            }
        }
        println(duplicates.joinToString("\n"))
//        print("Would you like PDFs as well? (Y/N): ")
//        when (scanner.nextLine().uppercase()) {
//            "Y" -> PdfConvertor().convert(docxPaths)
//        }
    }

    private fun addDonationsFromCSV(fileName: String, type: Donation.Type) {
        var bufferedReader: BufferedReader? = null
        try {
            val inputStream = File(System.getProperty("user.home"))
                .resolve("Desktop")
                .resolve(fileName)
                .inputStream()
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val firstLine = bufferedReader.readLine()
            if (firstLine != "Date,Name,Credit") {
                throw RuntimeException("first line of CSV '$fileName' should start with column names: Date,Name,Credit")
            }
            while (true) {
                val line = bufferedReader.readLine() ?: break
                val donation = Donation(line, type)
                if (donation.year != this.year) {
                    continue
                }
                val donations = donationsByName[donation.name] ?: ArrayList()
                donations.add(donation)
                donationsByName[donation.name] = donations
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}