import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

class PdfFactory(
    private val name: String,
    private val fein: String,
    private val year: Int,
    filename: String,
) : DocumentFactory {
    private val document = Document()
    private val defaultFont = Defaults.fontFamily
    private val outputPath = "${Defaults.outputDir}/$year/$filename.pdf"
    private val pdfWriter: PdfWriter

    init {
        File(outputPath).parentFile.mkdirs()
        pdfWriter = PdfWriter.getInstance(document, FileOutputStream(outputPath))
        document.open()
    }

    override fun addFooter() {
        pdfWriter.pageEvent = object : PdfPageEventHelper() {
            override fun onEndPage(writer: PdfWriter, document: Document) {
                ColumnText.showTextAligned(
                    writer.directContent,
                    Element.ALIGN_LEFT,
                    Phrase(
                        Defaults.footerText,
                        FontFactory.getFont(defaultFont, Defaults.fontSizeF, Font.BOLD, BaseColor.BLACK)
                    ),
                    36f,
                    28f,
                    0f
                )
            }
        }
    }

    override fun addReport(donations: List<Donation>) {
        FontFactory.getFont(defaultFont, Defaults.fontSizeF + 3, Font.BOLD, BaseColor.BLACK).let { font ->
            val p = Paragraph()
            p.alignment = Element.ALIGN_CENTER
            p.add(Chunk("$name\n", font))
            p.add(Chunk(" \n", font))
            p.add(Chunk("January through December $year Contribution Acknowledgement\n", font))
            p.add(Chunk(" \n", font))
            p.add(Chunk("FEIN $fein", font))
            p.add(Chunk(" \n", font))
            document.add(p)
        }
        document.add(Paragraph("\n\n"))
        val table = PdfPTable(3)
        table.setWidths(intArrayOf(2, 5, 3))
        val headerFont = FontFactory.getFont(defaultFont, Defaults.fontSizeF, Font.BOLD, BaseColor.BLACK)
        val cellFont = FontFactory.getFont(defaultFont, Defaults.fontSizeF, BaseColor.BLACK)
        table.addCell(
            text = "Date",
            font = headerFont,
            alignment = Element.ALIGN_CENTER,
            borderTop = false,
            borderLeft = false,
        )
        table.addCell(
            text = "Name",
            font = headerFont,
            alignment = Element.ALIGN_CENTER,
            borderTop = false,
        )
        table.addCell(
            text = "Credit",
            font = headerFont,
            alignment = Element.ALIGN_CENTER,
            borderTop = false,
            borderRight = false,
        )
        var total = BigDecimal.ZERO
        donations.forEach { donation ->
            total = total.add(donation.getAmount())
            table.addCell(text = donation.date, font = cellFont, alignment = Element.ALIGN_RIGHT, borderLeft = false)
            table.addCell(text = donation.name, font = cellFont)
            table.addCell(text = donation.credit, font = cellFont, alignment = Element.ALIGN_RIGHT, borderRight = false)
        }
        table.addCell(
            text = "Total",
            font = headerFont,
            alignment = Element.ALIGN_CENTER,
            borderBottom = false,
            borderLeft = false
        )
        table.addCell(text = "", font = cellFont, borderBottom = false)
        table.addCell(
            text = Donation.CURRENCY_FORMAT.format(total),
            font = cellFont,
            alignment = Element.ALIGN_RIGHT,
            borderBottom = false,
            borderRight = false
        )
        document.add(table)
    }

    override fun addPageBreak() {
        document.newPage()
    }

    override fun writeToFile(): String {
        document.close()
        println("pdf written successfully")
        return outputPath
    }

    private fun PdfPTable.addCell(
        text: String,
        font: Font,
        borderWidth: Float = 0.5f,
        padding: Float = 4f,
        alignment: Int = Element.ALIGN_LEFT,
        borderTop: Boolean = true,
        borderBottom: Boolean = true,
        borderLeft: Boolean = true,
        borderRight: Boolean = true,
    ) {
        addCell(PdfPCell().apply {
            borderWidthTop = if (borderTop) borderWidth else 0f
            borderWidthBottom = if (borderBottom) borderWidth else 0f
            borderWidthLeft = if (borderLeft) borderWidth else 0f
            borderWidthRight = if (borderRight) borderWidth else 0f
            setPadding(padding)
            phrase = Phrase(text, font)
            horizontalAlignment = alignment
        })
    }
}