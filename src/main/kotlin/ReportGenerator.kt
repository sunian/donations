import java.io.BufferedReader
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
        year = scanner.nextInt()
        addDonationsFromCSV("donations - check GFCC.csv", Donation.Type.CHECK)
        addDonationsFromCSV("donations - cash GFCC.csv", Donation.Type.CASH)
        WordDocFactory(year = year).apply {
            addFooter()
            donationsByName.keys.toList().sorted().forEach {
                addReport(donationsByName[it]!!.sorted())
            }
            writeToFile()
        }
    }

    private fun addDonationsFromCSV(fileName: String, type: Donation.Type) {
        var bufferedReader: BufferedReader? = null
        try {
            val inputStream = javaClass.getResourceAsStream(fileName)
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